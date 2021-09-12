package com.zypw.zypwgateway.securitymanager;

import com.zypw.zypwgateway.securityhandler.ReactiveSystemReactiveUserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
 */
@Component
@Slf4j
public class ReactiveSystemAuthenticationManager implements ReactiveAuthenticationManager {

    // for what???
    private Scheduler scheduler = Schedulers.parallel();

    private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Resource
    private ReactiveSystemReactiveUserDetailsServiceImpl userDetailsService;


    /**
     * @param authentication refer to the user input for login action!
     */
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        final String userAccount = authentication.getName();
        final String presentedPassword = (String) authentication.getCredentials();
        try {
            return this.userDetailsService.findByUsername(userAccount)
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
                    .map(u -> new UsernamePasswordAuthenticationToken(u, u.getPassword(), u.getAuthorities()));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error occurs when authenticating! Detail exception message:{}", e.getMessage());
            return null;
        }
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
