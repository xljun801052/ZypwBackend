package com.zypw.zypwgateway.securityhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zypw.zypwcommon.entity.authEntity.AuthUser;
import com.zypw.zypwcommon.entity.responseEntity.AxiosResult;
import com.zypw.zypwcommon.utils.JWTUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * handle when authenticate success!
 * */
@Component
@Slf4j
public class ReactiveSystemAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ObjectMapper objectMapper;

    private static final String TOKEN_PREFIX = "zypw:";
    private static final String REFRESH_TOKEN = "refresh_token";

    @SneakyThrows
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        DataBuffer buffer = null;
        try {
            // generate token and return it. Here we put a temporary fake token.
            // Note: the sensitive info can not be passed to user! use the objectMapper to serialize it!
            if (authentication.getPrincipal() instanceof AuthUser) {
                AuthUser user = (AuthUser) authentication.getPrincipal();
                String access_token = JWTUtils.sign(user.getId());
                if (access_token!=null) {

                    stringRedisTemplate.opsForValue().set(TOKEN_PREFIX+user.getId(), access_token, 10L, TimeUnit.DAYS);
                    log.info("access_token info：[userId:" + user.getId() + "  <--->  token:" + access_token);
                    stringRedisTemplate.opsForValue().set(user.getId() + "_refresh_token", access_token + REFRESH_TOKEN, 15L, TimeUnit.DAYS);
                    Map<String, Object> token_info = new HashMap<String, Object>();
                    token_info.put("access_token", access_token);
                    token_info.put("refresh_token", access_token + REFRESH_TOKEN);
                    buffer = response.bufferFactory().wrap(objectMapper.writeValueAsBytes(AxiosResult.success(token_info)));
                } else {
                    byte[] errorMsg = "error occurs when generate access_token, access_token can not be null!".getBytes(StandardCharsets.UTF_8);
                    buffer = response.bufferFactory().wrap(errorMsg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            byte[] errorMsg = "error occurs when handle authentication success!".getBytes(StandardCharsets.UTF_8);
            buffer = response.bufferFactory().wrap(errorMsg);
        }
        return response.writeWith(Mono.just(buffer));
    }
}
