package com.zypw.zypwauth.authorize;

import com.alibaba.fastjson.JSONObject;
import com.zypw.zypwauth.mapper.AuthorizeMapper;
import com.zypw.zypwcommon.entity.responseEntity.ResponseResult;
import com.zypw.zypwcommon.utils.JWTUtils;
import com.zypw.zypwcommon.entity.businessEntity.User;
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

    /**
     * 登录认证处理
     * */
    @PostMapping("/login")
    public String loginAuth(@RequestBody String jsonData) {
        Map<Object, Object> resultInfo = new HashMap<>();
        // 拿到用户登录信息参数
        // TODO: 2021/1/24 此处必须保证账号是唯一的，否则不能根据账户名来查询
        JSONObject data = (JSONObject) JSONObject.parse(jsonData);
        String username = data.get("username").toString();
        String password = data.get("password").toString();
        System.out.println("用户参数信息--》唯一账户:" + username + ",密码:" + password);
        // 比对数据库的用户名和密码信息，一致则予以登录成功，否则返回失败信息
        User user = authorizeMapper.findUserInfoByUsername(username);
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
        String token = JWTUtils.sign(Long.parseLong(user.getUserId().toString()));
        resultInfo.put("token", token);
        System.out.println("生成的信息：userId = "+user.getUserId().toString()+",token = " + token);
        stringRedisTemplate.opsForValue().set(user.getUserId().toString(), token);
        return JSONObject.toJSONString(resultInfo);
    }

    /**
     * 退出处理
     * */
    @PostMapping("/logout")
    public String logoutHandle(@RequestParam("userId") String userId) {
        Map<Object, Object> resultInfo = new HashMap<>();
        //step0:如果userId为空，提示异常,否则删除用户的redis缓存信息即可
        if (userId != null) {
            stringRedisTemplate.delete(userId);
            resultInfo.put("status", 216);
            resultInfo.put("msg", "退出成功");
        } else {
            resultInfo.put("status", 215);
            resultInfo.put("msg", "退出失败，用户状态异常");
        }
        return JSONObject.toJSONString(resultInfo);
    }

//    @PostMapping("/api")
//    public String apiAuth(HttpServletRequest request) {
//        System.out.println("微服務調用成功！");
//        return "success微服務調用成功！!";
//    }

}
