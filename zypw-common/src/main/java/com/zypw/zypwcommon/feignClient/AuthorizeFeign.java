package com.zypw.zypwcommon.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;

@FeignClient(value = "auth-service")
public interface AuthorizeFeign {

    @PostMapping("/login")
    String loginAuth(@RequestParam("userId") String userId, @RequestParam("password") String password);

    @PostMapping("/api")
    String apiAuth(HttpServletRequest request);
}
