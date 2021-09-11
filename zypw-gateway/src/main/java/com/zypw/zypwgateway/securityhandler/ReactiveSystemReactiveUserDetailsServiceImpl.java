package com.zypw.zypwgateway.securityhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zypw.zypwcommon.entity.authEntity.AuthUser;
import com.zypw.zypwcommon.feignClient.AuthorizeFeign;
import com.zypw.zypwcommon.feignClient.ProfileClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * for the authenticationManager to get the stored user info!
 */
@Slf4j
@Component
public class ReactiveSystemReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService, ReactiveUserDetailsPasswordService {

    private static final String USER_NOT_EXISTS = "current user not exists！";

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private AuthorizeFeign authorizeFeign;

    @Resource
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        UserDetails userDetails = new AuthUser(1,"xlys","{bcrypt}$2a$10$dfh8fdjlF2/3nKAFBsfqTOatlfSJhxjSC2Lrz/2CxjWwWQb1DW606","admin");
        return Mono.just(userDetails);
//        return Mono.just(authorizeFeign.getAuthenticationUser(username))
//                .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException(USER_NOT_EXISTS))))
//                .doOnNext(u -> log.info("query account successfully! user:{} ", u.getUsername() + u.getPassword()))
//                .cast(UserDetails.class);
    }

    @Override
    public Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
        return Mono.just(authorizeFeign.getAuthenticationUser(user.getUsername()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException(USER_NOT_EXISTS))))
                .map(foundedUser -> {
                    foundedUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
                    return foundedUser;
                })
                .flatMap(updatedUser -> {
                    String value = null;
                    try {
                        value = objectMapper.writeValueAsString(updatedUser);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return Mono.just(authorizeFeign.saveOrUpdateAuthUser(value));
                })
                .cast(UserDetails.class);
    }
}
