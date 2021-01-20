package com.xlys.zypwhomepage.controller;


import com.xlys.zypwhomepage.domain.Article;
import com.xlys.zypwhomepage.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 文章/帖子详情业务入口
 * */

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 获取单个文章详情
     */
    @RequestMapping("/detail/{id}")
    public Article getArticleDetail(@PathVariable("id") Integer id) {
        System.out.println("id = " + id);
        Article article = articleService.getArticleDetailInfoById(id);
        return article;
    }

    /**
     * 获取所有文章详情
     */
    @RequestMapping("/detail/all")
    public List<Article> getAllArticleDetail() {
        List<Article> articleList = articleService.getAllArticleDetail();
        return articleList;
    }
}
