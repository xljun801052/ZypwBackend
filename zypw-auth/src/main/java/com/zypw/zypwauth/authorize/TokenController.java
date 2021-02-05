package com.zypw.zypwauth.authorize;


import com.alibaba.fastjson.JSONObject;
import com.zypw.zypwcommon.entity.responseEntity.AxiosResult;
import com.zypw.zypwcommon.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 使用 access_token + refresh_token 进行token自动刷新认证
 * */
@RestController
@Slf4j
@RequestMapping("/refreshToken")
public class TokenController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 通过refresh_token刷新token返回，保持用户的登录状态
     * */
    @RequestMapping("/getNewtoken")
    public String refreshAccessToken(@RequestBody JSONObject jsonObject) {
        // 请求这个接口时说明access_token已经过期了,我们的目的就是要认证(变相做登录)成功之后重新给个access_token
        // --->这个说明referesh_token中必须要有认证的数据！这里以用户账号为key即可，暂不考虑安全问题
        String access_token = (String) jsonObject.get("access_token");
        Long userId = JWTUtils.verify(access_token);
        // 生成新的token并保存
        String new_access_token = JWTUtils.sign(Long.parseLong(userId.toString()));
        stringRedisTemplate.opsForValue().set(userId.toString(), new_access_token,1L, TimeUnit.HOURS);
        AxiosResult axiosResult = new AxiosResult(200,"更新token成功",new_access_token);
        return JSONObject.toJSONString(axiosResult);
    }

}
