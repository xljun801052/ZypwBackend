package com.xlys.zypwhomepage.controller;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xlys.zypwhomepage.domain.Article;
import com.xlys.zypwhomepage.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * 文章/帖子信息业务入口
 */

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 获取单个文章详情
     */
    @RequestMapping("/detail/{aid}")
    public String getArticleDetail(@PathVariable("aid") Integer aid, @RequestBody JSONObject jsonObject) {
        Integer userId = (Integer) jsonObject.get("userId");
        System.out.println("文章aid = " + aid);
        Article article = articleService.getArticleDetailInfoById(aid, userId);
        String jsonString = JSONObject.toJSONString(article, SerializerFeature.WriteNullNumberAsZero);
        return jsonString;
    }

    /**
     * 获取所有文章详情
     */
    @PostMapping("/detail/all")
    public String getAllArticleDetail() {
        List<Article> articleList = articleService.getAllArticleDetail();
        return JSONObject.toJSONString(articleList, SerializerFeature.WriteNullNumberAsZero);
    }

    /**
     * 获取所有文章评论详情
     */
    @RequestMapping("/comments/{id}")
    public String getAllArticleComments(@PathVariable("id") Integer id, @RequestBody JSONObject jsonObject) {
        Integer userId = (Integer) jsonObject.get("userId");
        List<HashMap<String, Object>> commentList = articleService.getAllArticleComments(id,userId);
        return JSONObject.toJSONString(commentList, SerializerFeature.WriteNullNumberAsZero);
    }

}
