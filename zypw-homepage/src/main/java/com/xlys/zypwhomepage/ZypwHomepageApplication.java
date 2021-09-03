package com.xlys.zypwhomepage;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.xlys.zypwhomepage.mapper")
public class ZypwHomepageApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZypwHomepageApplication.class, args);
    }

}
