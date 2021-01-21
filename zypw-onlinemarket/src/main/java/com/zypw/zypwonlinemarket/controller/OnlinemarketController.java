package com.zypw.zypwonlinemarket.controller;


import com.zypw.zypwcommon.feignClient.HomepageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/onlinemarket")
public class OnlinemarketController {

    @Autowired
    private HomepageClient client;

    @RequestMapping("/getInfo")
    public String getInfo() {
        System.out.println("onlinemarket服务被调用了...");
        System.out.println("准备调用homepage-service...");
        String allArticleDetail = client.getAllArticleDetail();
        System.out.println("allArticleDetail = " + allArticleDetail);
        System.out.println("完成调用homepage-service...");
        return allArticleDetail;
    }
}
