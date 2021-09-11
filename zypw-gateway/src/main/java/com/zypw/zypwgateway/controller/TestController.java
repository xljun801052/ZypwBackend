package com.zypw.zypwgateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@PreAuthorize("hasRole('ADMIN')")
public class TestController {

    @RequestMapping("/test/ping")
    public String getPong() {
        log.info("用户访问：{}",this.getClass().getSimpleName());
        return "pong!";
    }

}
