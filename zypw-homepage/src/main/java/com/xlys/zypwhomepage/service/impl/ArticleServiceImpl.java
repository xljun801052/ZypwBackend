package com.xlys.zypwhomepage.service.impl;

import com.xlys.zypwhomepage.domain.Article;
import com.xlys.zypwhomepage.mapper.ArticleMapper;
import com.xlys.zypwhomepage.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public Article getArticleDetailInfoById(Integer id) {
        Article article = articleMapper.getArticleDetailInfoById(id);
        return article;
    }
}
