package com.zypw.zypwgateway.client;

import com.zypw.zypwcommon.feignClient.HomepageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestServiceClient {

    @Autowired
    private HomepageClient client;

    /**
     * 测试feign调用homepage-service服务
     */
//    @RequestMapping(value = "/client/getArticleInfo", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    @RequestMapping(value = "/client/getArticleInfo", method = RequestMethod.GET)
    public String getArticleInfo() {
        System.out.println("不经过网关-准备调用homepage-service...");
        String allArticleDetail = client.getAllArticleDetail();
        System.out.println("allArticleDetail = " + allArticleDetail);
        System.out.println("不经过网关-完成调用homepage-service...");
        return allArticleDetail;
    }

    @RequestMapping(value = "/getArticleInfo", method = RequestMethod.GET)
    public String getArticleInfo2() {
        System.out.println("经过网关-准备调用homepage-service...");
        String allArticleDetail = client.getAllArticleDetail();
        System.out.println("allArticleDetail = " + allArticleDetail);
        System.out.println("经过网关-完成调用homepage-service...");
        return allArticleDetail;
    }
}
