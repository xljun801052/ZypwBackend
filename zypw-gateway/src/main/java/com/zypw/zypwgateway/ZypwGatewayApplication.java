package com.zypw.zypwgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * gateway网关服务：本系统目前只使用两个功能
 * 		1、权限认证(JWT)
 * 		2、统一路由跳转
 * */

@SpringBootApplication
@EnableDiscoveryClient
public class ZypwGatewayApplication {


	/**
	 * 这是一段spring cloud gateway的网关代码,展示了如何在代码中配置gateway的路由系统
	 * */
	/*@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("path_route", r -> r.path("/get")
						.uri("http://httpbin.org"))
				.route("host_route", r -> r.host("*.myhost.org")
						.uri("http://httpbin.org"))
				.route("rewrite_route", r -> r.host("*.rewrite.org")
						.filters(f -> f.rewritePath("/foo/(?<segment>.*)", "/${segment}"))
						.uri("http://httpbin.org"))
				.route("hystrix_route", r -> r.host("*.hystrix.org")
						.filters(f -> f.hystrix(c -> c.setName("slowcmd")))
						.uri("http://httpbin.org"))
				.route("hystrix_fallback_route", r -> r.host("*.hystrixfallback.org")
						.filters(f -> f.hystrix(c -> c.setName("slowcmd").setFallbackUri("forward:/hystrixfallback")))
						.uri("http://httpbin.org"))
				.route("limit_route", r -> r
						.host("*.limited.org").and().path("/anything/**")
						.filters(f -> f.requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())))
						.uri("http://httpbin.org"))
				.build();
	}*/

	public static void main(String[] args) {
		SpringApplication.run(ZypwGatewayApplication.class, args);
	}

}
