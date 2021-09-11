package com.zypw.zypwgateway.filter;

import com.zypw.zypwcommon.utils.JWTUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

//@Component
public class WebTokenFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = resolveToken(exchange.getRequest());
        if (Objects.nonNull(token)) {
            // TODO: 2021/9/11 token authenticate, use JwtProperties and JWTUtils
//            Authentication authentication = JWTUtils.verify("...");
//            chain.filter(exchange).subscriberContext(ReactiveSecurityContextHolder.withAuthentication(authentication));
        }
        return chain.filter(exchange);
    }

    private String resolveToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst("ZYPW_TOKEN");
        if (Objects.nonNull(token) && !token.equals("")) {
            return token;
        }
        return null;
    }
}
