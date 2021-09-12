package com.zypw.zypwgateway.securityhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zypw.zypwcommon.entity.responseEntity.AxiosResult;
import io.netty.util.CharsetUtil;
import lombok.SneakyThrows;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * handle when logout success!
 * */
@Component
public class ReactiveSystemAuthenticationLogoutSuccessHandler implements ServerLogoutSuccessHandler {

    @Resource
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
        ServerHttpResponse response = exchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        String result = objectMapper.writeValueAsString(AxiosResult.success("Logout Successfully!"));
        DataBuffer buffer = response.bufferFactory().wrap(result.getBytes(CharsetUtil.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

}
