package com.zypw.zypwgateway.securitymanager;

import com.zypw.zypwgateway.securityhandler.ReactiveSystemReactiveUserDetailsServiceImpl;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Resource;

/**
 * For authenticate the User
 * */
@Component
public class ReactiveSystemAuthenticationManager implements ReactiveAuthenticationManager {

    // for what???
    private Scheduler scheduler = Schedulers.parallel();

    private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Resource
    private ReactiveSystemReactiveUserDetailsServiceImpl userDetailsService;


    /**
     * @param  authentication refer to the user input
     * */
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        final String username = authentication.getName();
        final String presentedPassword = (String) authentication.getCredentials();
        return this.userDetailsService.findByUsername(username)
                .publishOn(this.scheduler)
                .filter(u -> this.passwordEncoder.matches(presentedPassword, u.getPassword()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Invalid Credentials"))))
                .flatMap(u -> {
                    boolean upgradeEncoding = this.userDetailsService != null
                            && this.passwordEncoder.upgradeEncoding(u.getPassword());
                    if (upgradeEncoding) {
                        String newPassword = this.passwordEncoder.encode(presentedPassword);
                        return this.userDetailsService.updatePassword(u, newPassword);
                    }
                    return Mono.just(u);
                })
                .map(u -> new UsernamePasswordAuthenticationToken(u, u.getPassword(), u.getAuthorities()) );
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
        this.passwordEncoder = passwordEncoder;
    }

    public void setScheduler(Scheduler scheduler) {
        Assert.notNull(scheduler, "scheduler cannot be null");
        this.scheduler = scheduler;
    }

}
