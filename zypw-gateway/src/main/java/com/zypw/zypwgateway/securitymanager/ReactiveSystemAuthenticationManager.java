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

import javax.annotation.Resource;

/**
 * For authenticate the User
 */
@Component
@Slf4j
public class ReactiveSystemAuthenticationManager implements ReactiveAuthenticationManager {

    private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Resource
    private ReactiveSystemReactiveUserDetailsServiceImpl userDetailsService;


    /**
     * @param authentication refer to the user input for login action!
     */
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        final String userAccount = authentication.getName();
        final String rawPassword = (String) authentication.getCredentials();
        try {
            return this.userDetailsService.findByUsername(userAccount)
                    .filter(u -> this.passwordEncoder.matches(rawPassword, u.getPassword()))
                    .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Invalid Credentials"))))
                    .flatMap(u -> {
                        boolean upgradeEncoding = this.userDetailsService != null
                                && this.passwordEncoder.upgradeEncoding(u.getPassword());
                        if (upgradeEncoding) {
                            String newPassword = this.passwordEncoder.encode(rawPassword);
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

}
