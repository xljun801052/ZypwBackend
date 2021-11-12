package com.zypw.zypwgateway.filter;

import com.alibaba.fastjson.JSON;
import com.zypw.zypwcommon.entity.responseEntity.ResponseResult;
import com.zypw.zypwcommon.utils.JWTUtils;
import com.zypw.zypwgateway.securityhandler.ReactiveSystemReactiveUserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * used to check token!
 */
@Component
@Slf4j
public class WebTokenFilter implements WebFilter {

    // Note: if the class is not in the SpringApplicationContext, then the @Autowired annotation won't get a stringRedisTemplate instance! It must be injected into the container
    // then can be accessed here!
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ReactiveSystemReactiveUserDetailsServiceImpl reactiveUserDetailsService;

    private static final String TOKEN_PREFIX = "zypw:";

    private static final String TOKEN_NAME = "access_token";



    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String access_token = resolveToken(exchange.getRequest());
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        if (null!=access_token) {
            // the token is not null, we verify it by JWTUtils.
            Long userId = JWTUtils.verify(access_token);
            if (userId != null) {
//                if (null==stringRedisTemplate) {
//                    stringRedisTemplate = exchange.getApplicationContext().getBean(StringRedisTemplate.class);
//                }
                String cachedAccessToken = stringRedisTemplate.opsForValue().get(TOKEN_PREFIX+userId);
                log.info("cached access_token = " + cachedAccessToken);
                // token is not in redis, it indicates that the token may expired!
                if (cachedAccessToken == null) {
                    serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                    DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(JSON.toJSONString(ResponseResult.TOKEN_EXPIRED).getBytes());
                    return serverHttpResponse.writeWith(Flux.just(dataBuffer));
                } else {
                    // token authentication passed!
                    if (cachedAccessToken.equals(access_token)) {
                        return chain.filter(exchange);
                    } else {
                        // token authentication failed!
                        serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                        DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(JSON.toJSONString(ResponseResult.TOKEN_INVALLID).getBytes());
                        return serverHttpResponse.writeWith(Flux.just(dataBuffer));
                    }
                }
            } else {
                serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(JSON.toJSONString(ResponseResult.USER_MISSING).getBytes());
                return serverHttpResponse.writeWith(Flux.just(dataBuffer));
            }
        } else {
            serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);//401, "Unauthorized"
            DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(JSON.toJSONString(ResponseResult.TOKEN_MISSING).getBytes());
            return serverHttpResponse.writeWith(Flux.just(dataBuffer));
        }
    }

    /**
     * parse Header and get raw token
     */
    private String resolveToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(TOKEN_NAME);
        return Objects.nonNull(token) && !token.equals("") ? token : null;
    }

}
