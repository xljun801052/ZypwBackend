package com.zypw.zypwgateway.filter;

import com.alibaba.fastjson.JSON;
import com.zypw.zypwcommon.entity.responseEntity.ResponseResult;
import com.zypw.zypwcommon.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.Objects;

/**
 * used to authenticate JWT!
 * */
@Component
@Slf4j
public class WebTokenFilter implements WebFilter {

    // Note: if the class is not in the SpringApplicationContext, then the @Autowired annotation won't get a stringRedisTemplate instance! It must be injected into the container
    // then can be accessed here!
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String access_token = resolveToken(exchange.getRequest());
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        if (Objects.nonNull(access_token)) {
            // the token is not null, we verify it by JWTUtils.
            Long userId = JWTUtils.verify(access_token);
            log.info("userId = " + userId);
            if (userId != null) {
                String cachedAccessToken = stringRedisTemplate.opsForValue().get(userId.toString()+"access_token");
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
                // 用户不存在，验证失败
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
     * */
    private String resolveToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst("ZYPW_TOKEN");
        return Objects.nonNull(token) && !token.equals("") ? token : null;
    }
}
