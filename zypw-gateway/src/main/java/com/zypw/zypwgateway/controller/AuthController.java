package com.zypw.zypwgateway.controller;

import com.zypw.zypwcommon.feignClient.AuthorizeFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author xlys
 * @since 参考文档：https://blog.csdn.net/a1036645146/article/details/109546416
 * 参考文档：https://www.jianshu.com/p/59295c91dde7【feign使用】
 */
@RequestMapping("/authorize")
public class AuthController {

    @Autowired
    private AuthorizeFeign authorizeFeign;
    /**
     * 登录认证
     * */
    @RequestMapping("/login")
    public String login(@RequestParam("userId") String userId, @RequestParam("password") String password) {
        String result = authorizeFeign.loginAuth(userId, password);
        return result;
    }

}
