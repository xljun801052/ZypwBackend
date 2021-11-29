package com.xlys.zypwhomepage.controller;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xlys.zypwhomepage.domain.Article;
import com.xlys.zypwhomepage.service.ArticleService;
import com.zypw.zypwcommon.entity.responseEntity.AxiosResult;
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

    @RequestMapping("/ping")
    public String ping() {
        return "pong!";
    }

    @Autowired
    private ArticleService articleService;


    // TODO: 2021/2/2 @RequestMapping注解是既可以接收GET方法也可以接收POST方法吗？还是。。。？

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
        List<Article> articles = articleService.getAllArticleDetail();
        return JSONObject.toJSONString(articles, SerializerFeature.WriteNullNumberAsZero);
    }

    @PostMapping("/detail/more")
    public AxiosResult getCurrentPageArticles(@RequestBody JSONObject params) {
        Integer pageSize = (Integer) params.get("pageSize");
        Integer currentPage = (Integer) params.get("currentPage") == 0 ? 1 : (Integer) params.get("currentPage");
        List<Article> articles = articleService.getCurrentPageArticlesAndTotalCount(pageSize, currentPage);
        Integer totalArticleCount = getTotalArticleCount();
        JSONObject data = new JSONObject(new HashMap<String, Object>() {{
            put("articles", articles);
            put("total", totalArticleCount);
        }});
        return new AxiosResult(200, "success", JSONObject.toJSONString(data, SerializerFeature.WriteNullNumberAsZero));
    }

    private Integer getTotalArticleCount() {
        return articleService.getTotalArticleCount();
    }

    /**
     * 获取所有文章评论详情
     */
    @RequestMapping("/comments/{id}")
    public String getAllArticleComments(@PathVariable("id") Integer id, @RequestBody JSONObject jsonObject) {
        Integer userId = (Integer) jsonObject.get("userId");
        List<HashMap<String, Object>> comments = articleService.getAllArticleComments(id, userId);
        return JSONObject.toJSONString(comments, SerializerFeature.WriteNullNumberAsZero);
    }


}
