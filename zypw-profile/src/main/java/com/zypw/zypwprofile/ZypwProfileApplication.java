package com.zypw.zypwprofile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.zypw.zypwcommon.entity")
public class ZypwProfileApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZypwProfileApplication.class, args);
    }

}
