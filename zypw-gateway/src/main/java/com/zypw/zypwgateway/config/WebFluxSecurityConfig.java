package com.zypw.zypwgateway.config;


import com.zypw.zypwgateway.converter.AuthenticationConverter;
import com.zypw.zypwgateway.securityhandler.*;
import com.zypw.zypwgateway.securitymanager.AuthenticationManager;
import com.zypw.zypwgateway.securitymanager.AuthorizationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

import javax.annotation.Resource;
import java.util.LinkedList;

@Configuration
@EnableWebFluxSecurity
public class WebFluxSecurityConfig {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private AuthorizationManager authorizeConfigManager;

    // The ExceptionTranslationFilter allows translation of AccessDeniedException and AuthenticationException into HTTP responses.
    // ExceptionTranslationFilter is inserted into the FilterChainProxy as one of the Security Filters.

    // If the application does not throw an AccessDeniedException or an AuthenticationException, then ExceptionTranslationFilter does not do anything.
    // The AuthenticationEntryPoint is used to request credentials from the client. For example, it might redirect to a login page or send a WWW-Authenticate header
    @Resource
    private AuthenticationEntryPointHandler authenticationEntryPointHandler;

    @Resource
    private JsonServerAuthenticationSuccessHandler authenticationSuccessHandler;

    @Resource
    private JsonServerAuthenticationFailureHandler authenticationFailureHandler;

    @Resource
    private JsonServerLogoutSuccessHandler logoutSuccessHandler;

    // configured the converter
    @Resource
    private AuthenticationConverter authenticationConverter;



    private static final String[] AUTH_WHITELIST = new String[]{"/login", "/logout"};

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        SecurityWebFilterChain chain = http.formLogin()
                .loginPage("/login")
                .authenticationSuccessHandler(authenticationSuccessHandler)
                .authenticationFailureHandler(authenticationFailureHandler)
                // 无访问权限handler
                .authenticationEntryPoint(authenticationEntryPointHandler)
                .and()

                .logout()
                .logoutSuccessHandler(logoutSuccessHandler)
                .and()

                .csrf().disable()
                .httpBasic().disable()

                .authorizeExchange()
                // 白名单放行
                .pathMatchers(AUTH_WHITELIST).permitAll()
                // 访问权限控制
                .anyExchange().access(authorizeConfigManager)
                .and().build();

        // 设置自定义登录参数转换器
        chain.getWebFilters()
                .filter(webFilter -> webFilter instanceof AuthenticationWebFilter)
                .subscribe(webFilter -> {
                    AuthenticationWebFilter filter = (AuthenticationWebFilter) webFilter;
                    filter.setServerAuthenticationConverter(authenticationConverter);
                });

        return chain;
    }


    // for multiple webFilterChains.Note:the less the order number is, the higher precedence the chains should be considered.
    // Spring Security will select one SecurityWebFilterChain for each request. It will match the requests in order by the securityMatcher definition
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    @Bean
//    SecurityWebFilterChain apiHttpSecurity(ServerHttpSecurity http) {
//        http
//                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/api/**"))
//                .authorizeExchange((exchanges) -> exchanges.anyExchange().authenticated())
//                .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt);
//        return http.build();
//    }

    /**
     * 注册用户信息验证管理器，可按需求添加多个按顺序执行
     *
     * @return
     */
    @Bean
    ReactiveAuthenticationManager reactiveAuthenticationManager() {
        LinkedList<ReactiveAuthenticationManager> managers = new LinkedList<>();
        managers.add(authenticationManager);
        return new DelegatingReactiveAuthenticationManager(managers);
    }


    /**
     * BCrypt密码编码
     *
     * @return
     */
    @Bean
    public BCryptPasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
