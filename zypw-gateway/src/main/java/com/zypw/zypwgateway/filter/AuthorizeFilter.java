package com.zypw.zypwgateway.filter;

import com.alibaba.fastjson.JSON;
import com.zypw.zypwcommon.entity.responseEntity.ResponseResult;
import com.zypw.zypwcommon.utils.JWTUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 自定义全局过滤器：进行token验证
 */
@Component
@CrossOrigin
public class AuthorizeFilter implements GlobalFilter, Ordered {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // step0:从请求中获取基本信息
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        String uri = serverHttpRequest.getURI().getPath();

        // TODO: 2021-02-04 这里有个BUG：每次首次登录时候都是下游服务有问题，然后503 Service Unavailable ，待解决一下！应该和网关与服务启动顺序是否有关？
        // 第二次再请求前台还是503错误，但是实际上已经验证成功生成了token并存放到了redis中了！
        // step1:进行uri白名单校验，如果在白名单中，则不进行token认证，直接通过
        if (uri.indexOf("/login") >= 0 || uri.indexOf("/logout") >= 0) {
            return chain.filter(exchange);
        }
        System.out.println("global-filter is working...");

        // step2：对于不在白名单中且需要进行token验证的请求进行token验证
        String token = serverHttpRequest.getHeaders().getFirst("token");
        System.out.println("token = " + token);
        // token为空,返回认证不通过【开发阶段可以把这几个返回报错区别一下】
        serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        // 这里token为"null"~_~
        if (StringUtils.isBlank(token)||token.equalsIgnoreCase("null")) {
            serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);//401, "Unauthorized"
            DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(JSON.toJSONString(ResponseResult.TOKEN_MISSING).getBytes());
            return serverHttpResponse.writeWith(Flux.just(dataBuffer));
        } else {
            // token不为空,进行token信息认证
            Long userId = JWTUtils.verify(token);
            System.out.println("userId = " + userId);
            if (userId != null) {
                String cacheToken = stringRedisTemplate.opsForValue().get(userId.toString());
                System.out.println("cacheToken = " + cacheToken);
                // token在缓存中没有，即token失效了
                if (cacheToken == null) {
                    serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                    DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(JSON.toJSONString(ResponseResult.TOKEN_EXPIRED).getBytes());
                    return serverHttpResponse.writeWith(Flux.just(dataBuffer));
                } else {
                    // token一致，通过此过滤器，予以放行,进行下一步流程
                    if (cacheToken.equals(token)) {
                        //serverHttpResponse.setStatusCode(HttpStatus.OK);
                        //DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(JSON.toJSONString(ResponseResult.SUCCESS).getBytes());
                        //return serverHttpResponse.writeWith(Flux.just(dataBuffer));
                        return chain.filter(exchange);
                    } else {
                        // token不一致，验证失败
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
        }
    }

    /**
     * 当一个请求请求与匹配某个路由时，过滤Web处理程序会将GlobalFilter的所有实例和GatewayFilter的所有特定于路由的实例添加到过滤器链中。
     * 该组合的过滤器链由org.springframework.core.Ordered接口排序，可以通过实现getOrder()方法进行设置。
     * <p>
     * 数值越小，优先级越高
     */

    @Override
    public int getOrder() {
        return -100;
    }

}
