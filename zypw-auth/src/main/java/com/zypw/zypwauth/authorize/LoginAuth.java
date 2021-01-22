package com.zypw.zypwauth.authorize;

import com.alibaba.fastjson.JSONObject;
import com.zypw.zypwauth.mapper.AuthorizeMapper;
import com.zypw.zypwauth.utils.JWTUtils;
import com.zypw.zypwcommon.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class LoginAuth {

    /**
     * Redis更新变化：spring boos 1.x使用jedis作为redisTemplate的客户端。spring boot 2.x使用lettuce作为redisTemplate的客户端。
     * Lettuce 和 Jedis 都是Redis的client，所以他们都可以连接 Redis Server。
     * Jedis在实现上是直接连接的Redis Server，如果在多线程环境下是非线程安全的。
     * 每个线程都去拿自己的 Jedis 实例，当连接数量增多时，资源消耗阶梯式增大，连接成本就较高了。
     * Lettuce的连接是基于Netty的，Netty 是一个多线程、事件驱动的 I/O 框架。连接实例可以在多个线程间共享，当多线程使用同一连接实例时，是线程安全的。
     * 所以，一个多线程的应用可以使用同一个连接实例，而不用担心并发线程的数量。
     * 当然这个也是可伸缩的设计，一个连接实例不够的情况也可以按需增加连接实例。
     * 通过异步的方式可以让我们更好的利用系统资源，而不用浪费线程等待网络或磁盘I/O。
     * 所以 Lettuce 可以帮助我们充分利用异步的优势。
     * 在spring boot 2.x。即使application.yaml中配置了spring.redis.jedis.pool....真正用的仍然是letturce。
     * 如果必须要切换，需要在starter中处理。
     * 配置了连接池和服务器的属性，Spring Boot的自动装配机制就会读取这些配置来生成有关Redis的操作对象。
     * 自动生成RedisConnectionFactory、RedisTemplate、StringRedisTemplate等常用的Redis操作对象。
     * RedisTemplate会默认使用JdkSerializationRedisSerializer进行序列化，然后存储到Redis中。如果需要在Redis中存储字符串，
     * 那么可以使用Spring Boot自动生成的StringRedisTemplate。
     */


    @Autowired
    private AuthorizeMapper authorizeMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/login")
    public String loginAuth(@RequestParam("userId") String userId, @RequestParam("password") String password) {
        Map<Object, Object> resultInfo = new HashMap<>();
        // 拿到用户登录信息参数
        System.out.println("用户信息--》账户:" + userId + ",密码:" + password);
        // 比对数据库的用户名和密码信息，一致则予以登录成功，否则返回失败信息
        User user = authorizeMapper.findUserInfoByUserId(userId);
        if (user == null) {
            System.out.println("用户不存在");
            resultInfo.put("status", 201);
            resultInfo.put("msg", "用户不存在");
            resultInfo.put("token", "");
            return JSONObject.toJSONString(resultInfo);
            // TODO: 2021-01-22 这里的密码需要使用md5加密加盐处理，不可以直接使用原生明文密码
        } else if (!user.getPassword().equals(password)) {
            System.out.println("密码错误");
            resultInfo.put("status", 202);
            resultInfo.put("msg", "密码错误");
            resultInfo.put("token", "");
            return JSONObject.toJSONString(resultInfo);
        }
        resultInfo.put("status", 200);
        resultInfo.put("msg", "登陆成功");
        // 登录成功生成jwt并返回，同时将用户数据放入Redis缓存中，设置过期时间
        String token = JWTUtils.sign(Long.parseLong(userId));
        resultInfo.put("token", token);
        stringRedisTemplate.opsForValue().set(userId, token);
        return JSONObject.toJSONString(resultInfo);
    }

    @PostMapping("/api")
    public String apiAuth(HttpServletRequest request) {
        Map<Object, Object> resultInfo = new HashMap<>();
        System.out.println("进行API接口调用权限认证");
        // 判断是否有jwt,没有则返回认证未通过
        String token = request.getHeader("JWT");
        if (token == null || token == "") {
            resultInfo.put("status", 203);
            resultInfo.put("msg", "token遗失");
            return JSONObject.toJSONString(resultInfo);
        } else {
            // 解析token,根据token中的userId从缓存中获取对应userId的token信息进行对比，进行判断
            Long userId = JWTUtils.verify(token);
            if (userId != null) {
                String cacheToken = stringRedisTemplate.opsForValue().get(userId);
                // token一致，予以放行,进行下一步流程
                if (cacheToken.equals(token)) {
                    resultInfo.put("status", 206);
                    resultInfo.put("msg", "OK~授权成功!准备下一环节的api调用权限认证");
                    return JSONObject.toJSONString(resultInfo);
                } else {
                    resultInfo.put("status", 205);
                    resultInfo.put("msg", "token与缓存token不一致");
                    return JSONObject.toJSONString(resultInfo);
                }
            } else {
                resultInfo.put("status", 204);
                resultInfo.put("msg", "非法token");
                return JSONObject.toJSONString(resultInfo);
            }
        }
    }

}
