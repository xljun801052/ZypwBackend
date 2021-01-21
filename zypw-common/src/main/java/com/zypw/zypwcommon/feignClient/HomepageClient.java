package com.zypw.zypwcommon.feignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "homepage-service")
public interface HomepageClient {


    /**
     * 获取所有文章详情
     */
    @GetMapping("/article/detail/all")
    String getAllArticleDetail();


    /**
     * 获取单个文章详情
     */
    @RequestMapping("/detail/{id}")
    String getArticleDetail(@PathVariable("id") Integer id);


    /**
     * 获取所有文章评论详情
     */
    @RequestMapping("/comments/{id}")
    String getAllArticleComments(@PathVariable("id") Integer id);
}
