package com.zypw.zypwgateway.securityhandler;

import com.zypw.zypwcommon.feignClient.ProfileClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@Slf4j
@Component
public class MySqlReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService, ReactiveUserDetailsPasswordService {

    private static final String USER_NOT_EXISTS = "用户不存在！";

    @Resource
    private ProfileClient profileClient;

    @Resource
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.just(profileClient.getAccountInfo(username))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException(USER_NOT_EXISTS))))
                .doOnNext(u -> log.info("查询账号成功  user:{} ", u.getUserName()+u.getPassword()))
                .cast(UserDetails.class);
    }

    @Override
    public Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
        return Mono.just(profileClient.getAccountInfo(user.getUsername()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException(USER_NOT_EXISTS))))
                .map(foundedUser -> {
                    foundedUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
                    return foundedUser;
                })
                .flatMap(updatedUser -> Mono.just(profileClient.saveUser(updatedUser)))
                .cast(UserDetails.class);
    }
}
