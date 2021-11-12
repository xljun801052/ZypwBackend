package com.xlys.zypwhomepage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

// to forbidden spring-security functionality. The feature has already been in gateway!
@SpringBootApplication(
        exclude = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class
        })
@EnableDiscoveryClient
@MapperScan("com.xlys.zypwhomepage.mapper")
public class ZypwHomepageApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZypwHomepageApplication.class, args);
    }

}
