package com.zypw.zypwgateway.config;


import com.zypw.zypwgateway.securityhandler.ReactiveSystemAuthenticationEntryPoint;
import com.zypw.zypwgateway.securityhandler.ReactiveSystemAuthenticationFailedHandler;
import com.zypw.zypwgateway.securityhandler.ReactiveSystemAuthenticationLogoutSuccessHandler;
import com.zypw.zypwgateway.securityhandler.ReactiveSystemAuthenticationSuccessHandler;
import com.zypw.zypwgateway.securitymanager.ReactiveSystemAuthenticationManager;
import com.zypw.zypwgateway.securitymanager.ReactiveSystemAuthorizationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerFormLoginAuthenticationConverter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * Note: different filter chain is used to protect different types of resources!
 * We configure multiple SecurityWebFilterChain instances to separate configuration by RequestMatcher[s].
 * A security filter chain (or, equivalently, a WebSecurityConfigurerAdapter) has a request matcher that is used to decide whether to apply it to an HTTP request.
 * <p>
 * One of the easiest mistakes to make when configuring Spring Security is to forget that these matchers
 * apply to different processes. One is a request matcher for the whole filter chain, and the other is only to choose the access rule to apply.
 * <p>
 * So the order of each filter chain is very important.for multiple webFilterChains.Note:the less the order number is, the higher precedence the chains should be considered.
 * Each set of resources has its own WebSecurityConfigurerAdapter with a unique order and its own request matcher.
 * If the matching rules overlap, the earliest ordered filter chain wins.
 */
@Configuration
@EnableWebFluxSecurity
public class SystemWebFluxSecurityConfig {

    @Resource
    private ReactiveSystemAuthenticationManager authenticationManager;

    @Resource
    private ReactiveSystemAuthorizationManager authorizationManager;

    // The ExceptionTranslationFilter allows translation of AccessDeniedException and AuthenticationException into HTTP responses.
    // ExceptionTranslationFilter is inserted into the FilterChainProxy as one of the Security Filters.

    // If the application does not throw an AccessDeniedException or an AuthenticationException, then ExceptionTranslationFilter does not do anything.
    // The AuthenticationEntryPoint is used to request credentials from the client. For example, it might redirect to a login page or send a WWW-Authenticate header

    @Resource
    private ReactiveSystemAuthenticationEntryPoint authenticationEntryPointHandler;

    @Resource
    private ReactiveSystemAuthenticationSuccessHandler authenticationSuccessHandler;

    @Resource
    private ReactiveSystemAuthenticationFailedHandler authenticationFailureHandler;

    @Resource
    private ReactiveSystemAuthenticationLogoutSuccessHandler logoutSuccessHandler;


    // TODO: 2021/9/12 add to configuration
    private static final String[] AUTH_WHITELIST = new String[]{"/login", "/logout"};

    /**
     * filter chain to protect web api resources!
     */
    @Order(HIGHEST_PRECEDENCE)
    @Bean
    public SecurityWebFilterChain webSecurityFilterChain(ServerHttpSecurity http) {
        SecurityWebFilterChain chain =
                http
                        .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/login"))
                        .csrf().disable()
                        .httpBasic().disable()
                        .authenticationManager(authenticationManager)
                        .exceptionHandling()
                        .authenticationEntryPoint(authenticationEntryPointHandler)
                        .accessDeniedHandler(
                                (swe, o) -> {
                                    swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                                    return swe.getResponse().writeWith(Mono.just(new DefaultDataBufferFactory().wrap("you are forbidden to access!".getBytes(StandardCharsets.UTF_8))));
                                }
                        )
                    .and()
                        .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                        .authorizeExchange()
                        .pathMatchers(AUTH_WHITELIST).permitAll()
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .anyExchange().authenticated()
                    .and()
                        .formLogin()
                        .loginPage("/login")
                        .authenticationSuccessHandler(authenticationSuccessHandler)
                        .authenticationFailureHandler(authenticationFailureHandler)
                    .and()
                        .logout()
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler)
                    .and()
//                .addFilterAt(new WebTokenFilter(), SecurityWebFiltersOrder.HTTP_BASIC) // add token filter
                        .build();


        // configure the customized input param converter
        chain.getWebFilters()
                .filter(webFilter -> webFilter instanceof AuthenticationWebFilter)
                .subscribe(webFilter -> {
                    AuthenticationWebFilter filter = (AuthenticationWebFilter) webFilter;
                    filter.setServerAuthenticationConverter(new ServerFormLoginAuthenticationConverter());
                });

        return chain;
    }


    // for multiple webFilterChains.Note:the less the order number is, the higher precedence the chains should be considered.
    // Spring Security will select one SecurityWebFilterChain for each request. It will match the requests in order by the securityMatcher definition

    /**
     * filter chain to do protect web security!
     */
//    @Order(HIGHEST_PRECEDENCE)
//    @Bean
//    SecurityWebFilterChain apiHttpSecurity(ServerHttpSecurity http) {
//        http
//                .csrf().disable()
//                .httpBasic().disable()
//                // set the match configuration for current filter chain
//                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/api/**", HttpMethod.POST))
//                .addFilterAt(new WebTokenFilter(), SecurityWebFiltersOrder.FIRST);
////                .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt);
//        return http.build();
//    }


    /**
     * set BCryptPasswordEncoder to secure the  rawPassword
     *
     * @return
     */
    @Bean
    public BCryptPasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
