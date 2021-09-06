package com.zypw.zypwgateway.securityhandler;

import com.alibaba.fastjson.JSONObject;
import com.zypw.zypwcommon.entity.responseEntity.AxiosResult;
import io.netty.util.CharsetUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationEntryPointHandler implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        String body = JSONObject.toJSONString(AxiosResult.error(e.getMessage()));
        DataBuffer wrap = exchange.getResponse().bufferFactory().wrap(body.getBytes(CharsetUtil.UTF_8));
        return exchange.getResponse().writeWith(Flux.just(wrap));
    }
}