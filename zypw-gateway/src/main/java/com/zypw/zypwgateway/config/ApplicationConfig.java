package com.zypw.zypwgateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * configuration  for the app!
 * */
@Configuration
@Slf4j
public class ApplicationConfig {


    private static void initApp() {
        // TODO: 2021/9/12 init the application!

    }

//    @Bean
//    StringRedisTemplate redisTemplate() {
//        StringRedisTemplate redisTemplate = new StringRedisTemplate();
//        redisTemplate.setConnectionFactory(jedisConnectionFactory());
//        log.info("Redis connected! {}",redisTemplate.opsForValue().get("ping"));
//        return redisTemplate;
//    }
//    @Bean(name="jedisConnectionFactory")
//    LettuceConnectionFactory jedisConnectionFactory() {
//        LettuceConnectionFactory factory = new LettuceConnectionFactory();
//        factory.setHostName("121.4.236.97");
//        factory.setPort(6379);
//        factory.setPassword("123789Xlys!@#$%");
//        return factory;
//    }
}
