package com.zypw.zypwgateway.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.zypw.zypwgateway.utils.JWTUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author xlys
 * @since
 * 参考文档：https://blog.csdn.net/a1036645146/article/details/109546416
 * 参考文档：https://www.jianshu.com/p/59295c91dde7【feign使用】
 *
 */
public class AuthController {

    /**
     * 登录认证
     * */
    @PostMapping("/auth/login")
    public Result login(HttpServletRequest request, @Valid @RequestBody LoginDTO loginDto) {

        String ip = IpAddressUtils.getIpAddr(request);
        // 获取用户信息、比对密码
        Result<UserDTO> result = loginFeignApi.login(loginDto,ip);
        if(ResultCode.SUCCESS.getCode()!=result.getCode()){
            log.error(result.getMsg());
            return result;
        }
        UserDTO user = result.getData();
        String token = JWTUtils.createJwt(user.getId() + "");
        data.put("token",token);
        return Result.success(data);
    }

}
