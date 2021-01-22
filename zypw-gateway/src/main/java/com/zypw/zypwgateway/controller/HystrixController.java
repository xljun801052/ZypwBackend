package com.zypw.zypwgateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HystrixController {

    /**
     * homepage熔断降级处理返回
     * */
    @RequestMapping("/homepage_fallback")
    public ResponseEntity homepageFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("homepage服务异常!");
    }

    /**
     * onlonemarket熔断降级处理返回
     * */
    @RequestMapping("/onlonemarket_fallback")
    public ResponseEntity onlonemarketFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("onlonemarket服务异常!");
    }

    /**
     * gateway熔断降级处理返回
     * */
    @RequestMapping("/gateway_fallback")
    public ResponseEntity gatewayFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("gateway服务异常!");
    }
}
