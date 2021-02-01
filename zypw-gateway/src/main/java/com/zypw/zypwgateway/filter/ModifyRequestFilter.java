package com.zypw.zypwgateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zypw.zypwcommon.utils.JWTUtils;
import io.netty.buffer.ByteBufAllocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;


/**
 * Gateway全局过滤器--修改请求参数过滤器：
 *      1、添加userId
 * */
@Component
@CrossOrigin
public class ModifyRequestFilter implements GlobalFilter, Ordered {

    private static Logger logger = LoggerFactory.getLogger("ModifyRequestFilter");

    private final DataBufferFactory dataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String uri = serverHttpRequest.getURI().getPath();
        if (uri.indexOf("/login") >= 0 || uri.indexOf("/logout") >= 0) {
            return chain.filter(exchange);
        }
        System.out.println("modifyRequest-filter is working...");
        ServerHttpRequest request = exchange.getRequest();
        // 拿到token,提取userId
        String token = request.getHeaders().getFirst("token");
        Integer userId = Math.toIntExact(JWTUtils.verify(token));
        if (!StringUtils.hasLength(token)) {
            throw new IllegalArgumentException("token");
        }
        // 新建一个ServerHttpRequest装饰器,覆盖需要装饰的方法
        ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(request) {

            @Override
            public Flux<DataBuffer> getBody() {
                Flux<DataBuffer> body = super.getBody();
                InputStreamHolder holder = new InputStreamHolder();
                body.subscribe(buffer -> holder.inputStream = buffer.asInputStream());
                if (null != holder.inputStream) {
                    try {
                        // 解析JSON的节点
                        JsonNode jsonNode = objectMapper.readTree(holder.inputStream);
                        Assert.isTrue(jsonNode instanceof ObjectNode, "JSON格式异常");
                        ObjectNode objectNode = (ObjectNode) jsonNode;
                        // JSON节点最外层写入新的属性
                        objectNode.put("userId", userId);
                        DataBuffer dataBuffer = dataBufferFactory.allocateBuffer();
                        String json = objectNode.toString();
                        logger.info("最终的JSON数据为:{}", json);
                        dataBuffer.write(json.getBytes(StandardCharsets.UTF_8));
                        return Flux.just(dataBuffer);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                } else {
                    return super.getBody();
                }
            }
        };
        // 使用修改后的ServerHttpRequestDecorator重新生成一个新的ServerWebExchange【因为直接获得的是一个readonly的request】
        return chain.filter(exchange.mutate().request(decorator).build());
    }

    private class InputStreamHolder {

        InputStream inputStream;
    }

    @Override
    public int getOrder() {
        return -99;
    }
}
