package com.xlys.zypwhomepage.controller;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xlys.zypwhomepage.domain.Article;
import com.xlys.zypwhomepage.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * 文章/帖子信息业务入口
 */

@RestController
@RequestMapping("/article")
@CrossOrigin
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 获取单个文章详情
     */
    @RequestMapping("/detail/{id}")
    public String getArticleDetail(@PathVariable("id") Integer id) {
        System.out.println("id = " + id);
        Article article = articleService.getArticleDetailInfoById(id);
        System.out.println("article = " + article);
        String jsonString = JSONObject.toJSONString(article, SerializerFeature.WriteNullNumberAsZero);
        System.out.println("文章" + id + "详情 = " + jsonString);
        return jsonString;
    }

    /**
     * 获取所有文章详情
     */
    @RequestMapping("/detail/all")
    public String getAllArticleDetail() {
        List<Article> articleList = articleService.getAllArticleDetail();
        return JSONObject.toJSONString(articleList, SerializerFeature.WriteNullNumberAsZero);
    }

    /**
     * 获取所有文章评论详情
     */
    @RequestMapping("/comments/{id}")
    public String getAllArticleComments(@PathVariable("id") Integer id) {
        List<HashMap<String,Object>> commentList = articleService.getAllArticleComments(id);
        return JSONObject.toJSONString(commentList, SerializerFeature.WriteNullNumberAsZero);
    }

}
