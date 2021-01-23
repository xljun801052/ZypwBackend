package com.zypw.zypwauth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zypw.zypwauth.mapper")
public class ZypwAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZypwAuthApplication.class, args);
    }

}
