package com.xlys.zypwhomepage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ZypwHomepageApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZypwHomepageApplication.class, args);
	}

}
