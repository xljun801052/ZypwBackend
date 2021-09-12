package com.zypw.zypwgateway.securityhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zypw.zypwcommon.entity.responseEntity.AxiosResult;
import io.netty.util.CharsetUtil;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * handle when authenticate fail!
 * */
@Component
public class ReactiveSystemAuthenticationFailedHandler implements ServerAuthenticationFailureHandler {

    @Resource
    private ObjectMapper objectMapper;

    private static final String USER_NOT_EXISTS = "Current user doesn't exist！";

    private static final String USERNAME_PASSWORD_ERROR = "Invalid username or password！";

    private static final String USER_LOCKED = "Current user is locked！";

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        if (exception instanceof UsernameNotFoundException) {
            return writeErrorMessage(response, USER_NOT_EXISTS);
        } else if (exception instanceof BadCredentialsException) {
            return writeErrorMessage(response, USERNAME_PASSWORD_ERROR);
        } else if (exception instanceof LockedException) {
            return writeErrorMessage(response, USER_LOCKED);
        }
        return writeErrorMessage(response, exception.getMessage());
    }

    @SneakyThrows
    private Mono<Void> writeErrorMessage(ServerHttpResponse response, String message) {
        String result = objectMapper.writeValueAsString(AxiosResult.error(String.format("Authentication failed!! Reason: %s",message)));
        return response.writeWith(Mono.just(response.bufferFactory().wrap(result.getBytes(CharsetUtil.UTF_8))));
    }
}
