package com.zypw.zypwgateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 这个过滤器解决body不能重复读的问题
 * 实际上这里没必要把body的内容放到attribute中去，因为从attribute取出body内容还是需要强转成
 * Flux<DataBuffer>,然后转换成String,和直接读取body没有什么区别
 *
 * CacheBodyGlobalFilter这个全局过滤器的目的就是把原有的request请求中的body内容读出来，
 * 并且使用ServerHttpRequestDecorator这个请求装饰器对request进行包装，重写getBody方法，
 * 并把包装后的请求放到过滤器链中传递下去。这样后面的过滤器中再使用exchange.getRequest().getBody()来获取body时，
 * 实际上就是调用的重载后的getBody方法，获取的最先已经缓存了的body数据。这样就能够实现body的多次读取了。
 */
@Component
public class CachedBodyFilter implements Ordered, GlobalFilter {

//  public static final String CACHE_REQUEST_BODY_OBJECT_KEY = "cachedRequestBodyObject";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (exchange.getRequest().getHeaders().getContentType() == null) {
            return chain.filter(exchange);
        } else {
            return DataBufferUtils.join(exchange.getRequest().getBody())
                    .flatMap(dataBuffer -> {
                        DataBufferUtils.retain(dataBuffer);
                        Flux<DataBuffer> cachedFlux = Flux
                                .defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
                        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(
                                exchange.getRequest()) {
                            @Override
                            public Flux<DataBuffer> getBody() {
                                return cachedFlux;
                            }
                        };
//            exchange.getAttributes().put(CACHE_REQUEST_BODY_OBJECT_KEY, cachedFlux);

                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    });
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
