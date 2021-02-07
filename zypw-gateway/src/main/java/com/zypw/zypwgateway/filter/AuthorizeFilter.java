package com.zypw.zypwgateway.filter;

import com.alibaba.fastjson.JSON;
import com.zypw.zypwcommon.entity.responseEntity.AxiosResult;
import com.zypw.zypwcommon.entity.responseEntity.ResponseResult;
import com.zypw.zypwcommon.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
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
 *      验证逻辑：
 *          如果access_token有效则认证直接通过
 *          如果access_token无效，根据access_token拿到用户名核心信息拼接"refresh_token"组成refresh_token对应的key检验refresh_token，如果refresh_token有效，则返回前端更新token的信息，让其重新请求更新token接口
 *          如果access_token和refresh_token均无效，返回前端通知其进行重新登录
 *
 */
@Component
@CrossOrigin
@Slf4j
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
        log.info("global-filter is working...");

        // step2：对于不在白名单中且需要进行token验证的请求进行token验证
        String access_token = serverHttpRequest.getHeaders().getFirst("access_token");
        log.info("access_token = " + access_token);
        serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        if (StringUtils.isBlank(access_token)||access_token.equalsIgnoreCase("null")) { // 这里增加对token为"null"的情况判断~_~
            serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);//401, "Unauthorized"
            DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(JSON.toJSONString(new AxiosResult(ResponseResult.TOKEN_MISSING)).getBytes());
            return serverHttpResponse.writeWith(Flux.just(dataBuffer));
        } else {
            // token不为空,进行token信息认证
            Long userId = JWTUtils.verify(access_token);
            log.info("token提取的userId：" + userId);
            if (userId != null) {
                String cacheToken = stringRedisTemplate.opsForValue().get(userId.toString());
                log.info("cacheToken:" + cacheToken);
                // token在缓存中没有，即token失效了,判断refresh_token情况
                if (cacheToken == null) {
                    String cacheRefreshToken = stringRedisTemplate.opsForValue().get(userId.toString()+"refresh_token");
                    // refresh_token失效通知前端进行重新登录，否则通知前端进行token更新
                    if (cacheRefreshToken != null) {
                        serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                        DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(JSON.toJSONString(new AxiosResult(ResponseResult.TOKEN_NEED_REFRESH)).getBytes());
                        return serverHttpResponse.writeWith(Flux.just(dataBuffer));
                    } else {
                        serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                        DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(JSON.toJSONString(new AxiosResult(ResponseResult.TOKEN_EXPIRED)).getBytes());
                        return serverHttpResponse.writeWith(Flux.just(dataBuffer));
                    }
                } else {
                    // token一致，通过校验
                    if (cacheToken.equals(access_token)) {
                        return chain.filter(exchange);
                    } else {
                        // token不一致，验证失败
                        serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                        DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(JSON.toJSONString(new AxiosResult(ResponseResult.TOKEN_INVALLID)).getBytes());
                        return serverHttpResponse.writeWith(Flux.just(dataBuffer));
                    }
                }
            } else {
                // 用户不存在，验证失败【其实也可能是非法token导致解析的用户信息不对导致用户查不到！】
                serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(JSON.toJSONString(new AxiosResult(ResponseResult.USER_MISSING)).getBytes());
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
