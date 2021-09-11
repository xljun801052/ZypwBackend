package com.zypw.zypwauth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.zypw.zypwcommon.entity")
public class ZypwAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZypwAuthApplication.class, args);
    }

}
