package com.xlys.zypwhomepage.service.impl;

import com.xlys.zypwhomepage.domain.Article;
import com.xlys.zypwhomepage.domain.Comment;
import com.xlys.zypwhomepage.mapper.ArticleMapper;
import com.xlys.zypwhomepage.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public Article getArticleDetailInfoById(Integer id) {
        Article article = articleMapper.getArticleDetailInfoById(id);
        return article;
    }

    @Override
    public List<Article> getAllArticleDetail() {
        List<Article> articleList = articleMapper.getAllArticleDetail();
        return articleList;
    }

    @Override
    public List<HashMap<String, Object>> getAllArticleComments(Integer id) {
        List<HashMap<String, Object>> commentList = articleMapper.getAllArticleComments(id);
        return commentList;
    }
}
