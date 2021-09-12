package com.zypw.zypwgateway.securityhandler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * for alert user to input the info to authenticate!
 * */
@Component
public class ReactiveSystemAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        // illegal request, redirect to the login page first!
        response.setStatusCode(HttpStatus.SEE_OTHER);
        // TODO: 2021/9/12 configure the loginPage url to application.yml
        response.getHeaders().set("Location", "http://localhost:12019/login");
        return exchange.getResponse().setComplete();
    }
}
