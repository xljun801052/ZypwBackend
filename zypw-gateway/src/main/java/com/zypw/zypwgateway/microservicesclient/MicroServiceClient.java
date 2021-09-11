package com.zypw.zypwgateway.microservicesclient;

import com.zypw.zypwcommon.entity.authEntity.AuthUser;
import com.zypw.zypwcommon.feignClient.AuthorizeFeign;
import com.zypw.zypwcommon.feignClient.HomepageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Slf4j
public class MicroServiceClient {

    @Resource
    private HomepageClient client;

    @Resource
    private AuthorizeFeign authorizeFeign;

    /**
     * 测试feign调用homepage-service服务
     */
//    @RequestMapping(value = "/client/getArticleInfo", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    @RequestMapping(value = "/client/getArticleInfo", method = RequestMethod.GET)
    public String getArticleInfo() {
        log.info("不经过网关-准备调用homepage-service...");
        String allArticleDetail = client.getAllArticleDetail();
        log.info("allArticleDetail = " + allArticleDetail);
        log.info("不经过网关-完成调用homepage-service...");
        return allArticleDetail;
    }

    @RequestMapping(value = "/getArticleInfo", method = RequestMethod.GET)
    public String getArticleInfo2() {
        log.info("经过网关-准备调用homepage-service...");
        String allArticleDetail = client.getAllArticleDetail();
        log.info("allArticleDetail = " + allArticleDetail);
        log.info("经过网关-完成调用homepage-service...");
        return allArticleDetail;
    }

    @PostMapping("/getAuthUser/{userAccount}")
    AuthUser getAuthenticationUser(@PathVariable("userAccount") String userAccount) {
        log.info("经过网关-准备调用auth-service...");
        AuthUser user = authorizeFeign.getAuthenticationUser(userAccount);
        log.info("user = " + user);
        log.info("经过网关-完成调用auth-service...");
        return user;
    }
}
