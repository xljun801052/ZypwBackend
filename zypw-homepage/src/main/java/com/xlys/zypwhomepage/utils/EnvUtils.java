package com.xlys.zypwhomepage.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * utils for getting the [environment info] && [system info] && [application properties]
 */
@Slf4j
@RestController
public class EnvUtils {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @RequestMapping("/systemActiveProfile")
    public String getSystemActiveProfile() {
        return String.format("The system active profile:[%s]",activeProfile);
    }
}
