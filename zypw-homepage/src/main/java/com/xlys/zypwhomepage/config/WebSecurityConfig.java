package com.xlys.zypwhomepage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private static final String[] AUTH_WHITELIST = new String[]{"**/**"};

    @Order(HIGHEST_PRECEDENCE)
    @Bean
    public SecurityWebFilterChain webSecurityFilterChain(ServerHttpSecurity http) {
        SecurityWebFilterChain chain =
                http
                        .csrf().disable()
                        .httpBasic().disable()
                        .authorizeExchange()
                        .pathMatchers(AUTH_WHITELIST).permitAll()
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .anyExchange().authenticated()
                        .and()
                        .build();
        return chain;

    }
}
