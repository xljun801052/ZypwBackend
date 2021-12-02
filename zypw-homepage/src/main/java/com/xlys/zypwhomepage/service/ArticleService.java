package com.xlys.zypwhomepage.service;

import com.xlys.zypwhomepage.domain.Article;

import java.util.List;

public interface ArticleService {
    Article getArticleDetailInfoById(Integer aid,Integer userId);

    List<Article> getAllArticleDetail();

    List<Article> getCurrentPageArticlesAndTotalCount(Integer pageSize, Integer currentPage);

    Integer getTotalArticleCount();
}
