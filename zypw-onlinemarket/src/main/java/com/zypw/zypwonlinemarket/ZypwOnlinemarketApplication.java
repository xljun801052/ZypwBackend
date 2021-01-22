package com.zypw.zypwonlinemarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.zypw.zypwcommon.feignClient"})
public class ZypwOnlinemarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZypwOnlinemarketApplication.class, args);
    }

}
