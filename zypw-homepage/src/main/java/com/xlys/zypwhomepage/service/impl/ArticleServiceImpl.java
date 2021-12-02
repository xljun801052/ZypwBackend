package com.xlys.zypwhomepage.service.impl;

import com.xlys.zypwhomepage.domain.Article;
import com.xlys.zypwhomepage.mapper.ArticleMapper;
import com.xlys.zypwhomepage.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public Article getArticleDetailInfoById(Integer aid, Integer userId) {
        Article article = articleMapper.getArticleDetailInfoById(aid, userId);
        return article;
    }

    @Override
    public List<Article> getAllArticleDetail() {
        List<Article> articleList = articleMapper.getAllArticleDetail();
        return articleList;
    }

    @Override
    public List<Article> getCurrentPageArticlesAndTotalCount(Integer pageSize, Integer currentPage) {
        List<Article> articles = articleMapper.getCurrentPageArticlesAndTotalCount(pageSize, (currentPage - 1) * pageSize);
        if (articles == null) {
            return Collections.emptyList();
        }
        return articles;
    }

    @Override
    public Integer getTotalArticleCount() {
        return articleMapper.getTotalArticleCount();
    }

}
