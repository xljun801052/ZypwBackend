package com.zypw.zypwgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * Gateway Functionalities:
 *  Normal Use Cases
 *      ①HTTP Request
 *      ②HTTP Response
 *      ③Path
 *      ④Request Size Limit
 *      ⑤Related to HTTP Status
 *  Advanced Use Cases
 *      ①Request Rate Limiter
 *      ②Save Session and Secure Headers
 *      ③Retry
 *      ④Circuit Breaker
 *
 *  Current used:
 *      ①JWT
 *      ②Path: It is possible to set a different path (SetPath), rewrite (RewritePath), add a prefix (PrefixPath), and strip (StripPrefix) to extract only parts of it.
 *
 *  Integration with Spring-Security:
 *      spring security设置要采用响应式配置，基于WebFlux中WebFilter实现，与Spring MVC的Security是通过Servlet的Filter实现类似，也是一系列filter组成的过滤链。
 *      Reactor与传统MVC配置对应：
 *
 *      webflux	                                            mvc	                                作用
 *
 *      @EnableWebFluxSecurity                              @EnableWebSecurity	                开启security配置
 *      ServerAuthenticationSuccessHandler	                AuthenticationSuccessHandler	    登录成功Handler
 *      ServerAuthenticationFailureHandler	                AuthenticationFailureHandler	    登陆失败Handler
 *      ReactiveAuthorizationManager<AuthorizationContext>	AuthorizationManager	            认证管理
 *      ServerSecurityContextRepository	                    SecurityContextHolder	            认证信息存储管理
 *      ReactiveUserDetailsService	                        UserDetailsService	                用户登录
 *      ReactiveAuthorizationManager	                    AccessDecisionManager	            鉴权管理
 *      ServerAuthenticationEntryPoint	                    AuthenticationEntryPoint	        未认证Handler
 *      ServerAccessDeniedHandler	                        AccessDeniedHandler	                鉴权失败Handler
 *
 */

@SpringBootApplication
@EnableDiscoveryClient
//开启对Feign的支持，同时扫描接口生成代理类
@EnableFeignClients(basePackages = {"com.zypw.zypwcommon.feignClient"})
public class ZypwGatewayApplication {


    /**
     * 这是一段spring cloud gateway的网关代码,展示了如何在代码中配置gateway的路由系统
     */
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

    /**
     * 配置跨域
     * @return
     */
    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // cookie跨域
        config.setAllowCredentials(Boolean.TRUE);
        config.addAllowedMethod("*");
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        // 配置前端js允许访问的自定义响应头
        config.addExposedHeader("Authorization:");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }

    public static void main(String[] args) {
        SpringApplication.run(ZypwGatewayApplication.class, args);
    }

}
