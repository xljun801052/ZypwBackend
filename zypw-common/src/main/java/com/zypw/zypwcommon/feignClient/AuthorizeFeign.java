package com.zypw.zypwcommon.feignClient;

import com.zypw.zypwcommon.entity.authEntity.AuthUser;
import lombok.SneakyThrows;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import javax.servlet.http.HttpServletRequest;

@FeignClient(value = "zypw-auth-service")
public interface AuthorizeFeign {

    @PostMapping("/login")
    String loginAuth(@RequestParam("userId") String userId, @RequestParam("password") String password);

    @PostMapping("/getAuthUser/{userAccount}")
    AuthUser getAuthenticationUser(@PathVariable("userAccount") String userAccount);

    @SneakyThrows
    @RequestMapping("/save")
    AuthUser saveOrUpdateAuthUser(String authUserInfo);
}
