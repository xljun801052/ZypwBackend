package com.zypw.zypwgateway.securitymanager;

import com.zypw.zypwgateway.securityhandler.MySqlReactiveUserDetailsServiceImpl;
import com.zypw.zypwgateway.token.GeneralAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final Scheduler scheduler = Schedulers.parallel();

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private MySqlReactiveUserDetailsServiceImpl mySqlReactiveUserDetailsService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        GeneralAuthenticationToken token = (GeneralAuthenticationToken) authentication;
        final String username = authentication.getName();
        final String presentedPassword = (String) authentication.getCredentials();
        final String tenant = token.getTenant();
        final String host = token.getHost();
        return retrieveUser(username)
                .publishOn(scheduler) //?
                .filter(u -> passwordEncoder.matches(presentedPassword, u.getPassword()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Invalid Credentials"))))
                .flatMap(u -> {
                    boolean upgradeEncoding = mySqlReactiveUserDetailsService != null
                            && passwordEncoder.upgradeEncoding(u.getPassword());
                    if (upgradeEncoding) {
                        String newPassword = passwordEncoder.encode(presentedPassword);
                        return mySqlReactiveUserDetailsService.updatePassword(u, newPassword);
                    }
                    return Mono.just(u);
                })
                .flatMap(userDetails -> {
                    // 省略业务代码
                    return Mono.just(userDetails);
                })
                .map(u -> new GeneralAuthenticationToken(u, u.getPassword(), u.getAuthorities()));
    }

    protected Mono<UserDetails> retrieveUser(String username) {
        return mySqlReactiveUserDetailsService.findByUsername(username);
    }


}
