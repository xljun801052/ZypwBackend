package com.zypw.zypwgateway.securityhandler;

import com.alibaba.fastjson.JSONObject;
import com.zypw.zypwcommon.entity.responseEntity.AxiosResult;
import io.netty.util.CharsetUtil;
import org.springframework.core.io.buffer.DataBuffer;
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

/**
 * handle the case: when does not pass the authentication!
 * */
@Component
public class JsonServerAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {

    private static final String USER_NOT_EXISTS = "用户不存在！";

    private static final String USERNAME_PASSWORD_ERROR = "用户密码错误！";

    private static final String USER_LOCKED = "用户锁定！";

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

    private Mono<Void> writeErrorMessage(ServerHttpResponse response, String message) {
        String result = JSONObject.toJSONString(AxiosResult.error(message));
        DataBuffer buffer = response.bufferFactory().wrap(result.getBytes(CharsetUtil.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
