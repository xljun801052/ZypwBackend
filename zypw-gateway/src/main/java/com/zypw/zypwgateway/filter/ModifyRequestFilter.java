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
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Gateway全局过滤器--修改请求参数过滤器：
 * 1、添加userId
 */
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
        // 进行请求白名单放行
        String uriPath = serverHttpRequest.getURI().getPath();
        if (uriPath.indexOf("/login") >= 0 || uriPath.indexOf("/logout") >= 0) {
            return chain.filter(exchange);
        }
        System.out.println("modifyRequest-filter is working...");
        // 拿到token,提取userId，带到下游
        String token = serverHttpRequest.getHeaders().getFirst("token");
        Integer userId = Math.toIntExact(JWTUtils.verify(token));
        if (!StringUtils.hasLength(token)) {
            throw new IllegalArgumentException("token");
        }
        // 针对get和post等不同方法进行请求体获取
        String methodValue = serverHttpRequest.getMethodValue();
        // post请求获取请求体
        // 获取Post请求体的方法，无论是application/x-www-form-urlencoded和application/json都可以通过上面的方式。
        if (methodValue.equalsIgnoreCase("POST")) {
            String bodyStr = resolveBodyFromRequest1(serverHttpRequest);
            System.out.println("bodyStr = " + bodyStr);
            // 添加新的userId到requestBody中去
            JsonNode jsonNode = null;
            String newBodyStr = null;
            try {
                jsonNode = objectMapper.readTree(bodyStr);
                Assert.isTrue(jsonNode instanceof ObjectNode, "json数据异常");
                ObjectNode newBodyObjectNode = ((ObjectNode) jsonNode).put("userId", userId);
                newBodyStr = newBodyObjectNode.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //下面的将请求体再次封装写回到request里，传到下一级，否则，由于请求体已被消费，后续的服务将取不到值
            URI uri = serverHttpRequest.getURI();
            ServerHttpRequest request = serverHttpRequest.mutate().uri(uri).build();
            // 将请求体由String转成DataBuffer形式，封装成Flux<DataBuffer>,否则普通的String是无法在响应式中被框架消费的
            DataBuffer bodyDataBuffer = this.stringBuffer(newBodyStr);
            Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);
            // 新建一个ServerHttpRequestDecorator装饰器,覆盖需要装饰的方法
            /*request = new ServerHttpRequestDecorator(request) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return bodyFlux;
                }
            };*/
            // 由于修改了body,所以还需要修改对应的content-length
            // 定义新的消息头
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(exchange.getRequest().getHeaders());
            // 由于修改了传递参数，需要重新设置CONTENT_LENGTH，长度是字节长度，不是字符串长度
            int length = newBodyStr.getBytes().length;
            headers.remove(HttpHeaders.CONTENT_LENGTH);
            headers.setContentLength(length);
            headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
            request = new ServerHttpRequestDecorator(request) {
                @Override
                public HttpHeaders getHeaders() {
                    return headers;
                }

                @Override
                public Flux<DataBuffer> getBody() {
                    return bodyFlux;
                }
            };
            // 封装request，传给下游
            // 使用修改后的ServerHttpRequestDecorator重新生成一个新的ServerWebExchange【因为直接获得的是一个readonly的request】
            return chain.filter(exchange.mutate().request(request).build());
        } else if ("GET".equalsIgnoreCase(methodValue)) {
            Map requestQueryParams = serverHttpRequest.getQueryParams();
            //TODO 得到Get请求的请求参数后，just do something you want!

            return chain.filter(exchange);
        }
        return chain.filter(exchange);
    }

    /**
     * 从原始请求中获取请求体String形式数据
     */
    private String resolveBodyFromRequest1(ServerHttpRequest serverHttpRequest) {
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(dataBuffer -> {
            CharBuffer bodyCharBuffer = StandardCharsets.UTF_8.decode(dataBuffer.asByteBuffer());
            // TODO: 2021-02-03 后续了解一下DataBufferUtils的API，这步操作到底为什么？
            // 另外需要注意的是在我们创建ByteBuf对象后，它的引用计数是1，
            // 当你每次调用DataBufferUtils.release之后会释放引用计数对象时，它的引用计数减1，
            // 如果引用计数为0，这个引用计数对象会被释放（deallocate）,并返回对象池。
            // 当尝试访问引用计数为0的引用计数对象会抛出IllegalReferenceCountException异常如下:
            //              io.netty.util.IllegalReferenceCountException: refCnt: 0
            DataBufferUtils.release(dataBuffer);
            bodyRef.set(bodyCharBuffer.toString());
        });
        // 获取request body
        return bodyRef.get();
    }

    /**
     * 读取body内容
     * @param serverHttpRequest
     * @return
     */
    public static String resolveBodyFromRequest2(ServerHttpRequest serverHttpRequest){
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        StringBuilder sb = new StringBuilder();

        body.subscribe(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
//            DataBufferUtils.release(buffer);
            String bodyString = new String(bytes, StandardCharsets.UTF_8);
            sb.append(bodyString);
        });
        return formatStr(sb.toString());
    }

    /**
     * 去掉空格,换行和制表符
     * @param str
     * @return
     */
    private static String formatStr(String str){
        if (str != null && str.length() > 0) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            return m.replaceAll("");
        }
        return str;
    }

    /**
     * 将String字符串转换成DataBuffer
     */
    private DataBuffer stringBuffer(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);

        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }


    @Override
    public int getOrder() {
        return -99;
    }
}
