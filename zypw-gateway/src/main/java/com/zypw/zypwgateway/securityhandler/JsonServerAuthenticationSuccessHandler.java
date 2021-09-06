package com.zypw.zypwgateway.securityhandler;

import com.alibaba.fastjson.JSONObject;
import com.zypw.zypwcommon.entity.responseEntity.AxiosResult;
import io.netty.util.CharsetUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * handle the case: when pass the authentication!
 * */
@Component
public class JsonServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        // 登录成功后可以放入一些参数到session中
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        String body = JSONObject.toJSONString(AxiosResult.success("登录成功！"));
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(CharsetUtil.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
