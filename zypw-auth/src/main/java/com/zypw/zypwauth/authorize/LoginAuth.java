package com.zypw.zypwauth.authorize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zypw.zypwauth.mapper.AuthorizeMapper;
import com.zypw.zypwcommon.entity.responseEntity.AxiosResult;
import com.zypw.zypwcommon.utils.JWTUtils;
import com.zypw.zypwcommon.entity.businessEntity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
@Slf4j
public class LoginAuth {

    /**
     * Redis更新变化：spring boot 1.x使用jedis作为redisTemplate的客户端。spring boot 2.x使用lettuce作为redisTemplate的客户端。
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
     */
////    @PostMapping("/login")
////    public String loginAuth(@RequestBody String jsonData) {
////
////        // 拿到用户登录信息参数
////        // TODO: 2021/1/24 此处必须保证账号是唯一的，否则不能根据账户名来查询
////        JSONObject data = (JSONObject) JSONObject.parse(jsonData);
////        String username = data.get("username").toString();
////        String password = data.get("password").toString();
////        log.info("用户参数信息--》唯一账户:" + username + ",密码:" + password);
////        // 比对数据库的用户名和密码信息，一致则予以登录成功，否则返回失败信息
////        User user = authorizeMapper.findUserInfoByUsername(username);
////        if (user == null) {
////            AxiosResult axiosResult = new AxiosResult(201, "用户不存在", "");
////            return JSONObject.toJSONString(axiosResult);
////            // TODO: 2021-01-22 这里的密码需要使用md5加密加盐处理，不可以直接使用原生明文密码
////        } else if (!user.getPassword().equals(password)) {
////            AxiosResult axiosResult = new AxiosResult(202, "密码错误", "");
////            return JSONObject.toJSONString(axiosResult);
////        }
////        // *登录成功生成access_token和refresh_token并返回，同时将用户数据和refresh_token【每个用户的refresh_token唯一】放入Redis缓存中，为了安全需要设置过期时间,且access_token过期时间比refresh_token短。
////        // *这里有几种做法，可以评估一下：
////        //      ①将refresh_token和access_token一起存储，这样只需要一次redis链接操作--->不可行，access_token过期了，refresh_token也没了
////        //      ②将access_token，refresh_tokenf分别单独存储，过期时间也不一样--->问题是？这两个token怎么能与同一用户关联【要是redis可以做到一个map中部分信息过期就好了？研究一下】
////        //            首先要保证key唯一：分布式中的雪花算法不适用，hash算法是否已经够用，和md5有啥性能区别吗？貌似账户就可以，因为账户名就是唯一的啊，这里先不考虑加密算法安全问题
////        //            同时要保证refresh_token和access_token都存在不能覆盖且有关联--->考虑根据唯一性KEY生成另一个唯一性KEY:我们用账户名字面量+"refresh_token"来生成！
////        // *生成access_token并保存,生产上设置1hours过期，必须重新登录。主流网站的token过期时间，一般不超过1h。
////        String access_token = JWTUtils.sign(Long.parseLong(user.getUserId().toString()));
////        stringRedisTemplate.opsForValue().set(user.getUserId().toString(), access_token, 1L, TimeUnit.HOURS);
////        log.info("access_token对应的信息：[userId:" + user.getUserId().toString() + "  <--->  token:" + access_token);
////        // *生成refresh_token并保存--->//
////        stringRedisTemplate.opsForValue().set(user.getUserId().toString() + "refresh_token", access_token + "refresh_token", 15L, TimeUnit.DAYS);
////        JSONObject token_date = new JSONObject();
////        token_date.put("access_token", access_token);
////        AxiosResult axiosResult = new AxiosResult(200, "登陆成功", token_date);
////        return JSONObject.toJSONString(axiosResult);
////    }
//
//    /**
//     * 退出处理
//     */
////    @PostMapping("/logout")
////    public String logoutHandle(@RequestBody JSONObject jsonObject) {
////        String access_token = (String) jsonObject.get("access_token");
////        Long userId = JWTUtils.verify(access_token);
////        AxiosResult axiosResult = null;
////        //step0:如果userId为空，提示异常,否则删除用户的redis缓存信息即可
////        if (userId != null) {
////            // TODO: 2021-02-07 这里有个bug:redis的数据没有被删除掉？？还是可以看到的--->redis缓存序列化机制
////            stringRedisTemplate.delete(userId.toString());
////            stringRedisTemplate.delete(userId+"refresh_token");
////            axiosResult = new AxiosResult(216, "退出成功", "");
////        } else {
////            axiosResult = new AxiosResult(215, "退出失败，用户状态异常", "");
////        }
////        return JSONObject.toJSONString(axiosResult);
////    }

}
