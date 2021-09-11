package com.zypw.zypwgateway.securityhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.Objects;
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

    @SneakyThrows
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.getHeaders().set(HttpHeaders.CACHE_CONTROL, "no-store,no-cache,must-revalidate,max-age-8");
        DataBuffer buffer = null;
        try {
            // TODO: 2021/9/11 create the access_token and refresh_token  and save them to redis
            // generate token and return it. Here we put a temporary fake token.
            // Note: the sensitive info can not be passed to user! use the objectMapper to serialize it!
            if (authentication.getPrincipal() instanceof AuthUser) {
                AuthUser user = (AuthUser) authentication.getPrincipal();
                String access_token = JWTUtils.sign(Long.parseLong(user.getId().toString()));
                if (Objects.nonNull(access_token)) {
                    stringRedisTemplate.opsForValue().set(user.getId().toString(), access_token, 1L, TimeUnit.HOURS);
                    log.info("access_token info：[userId:" + user.getId() + "  <--->  token:" + access_token);
                    stringRedisTemplate.opsForValue().set(user.getId() + "_refresh_token", access_token + "refresh_token", 15L, TimeUnit.DAYS);
                    Map<String, Object> token_info = new HashMap<String, Object>();
                    token_info.put("access_token", access_token);
                    token_info.put("refresh_token", access_token + "refresh_token");
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
