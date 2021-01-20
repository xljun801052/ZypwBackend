package com.xlys.zypwhomepage.service;

import com.xlys.zypwhomepage.domain.Article;

import java.util.List;

public interface ArticleService {
    Article getArticleDetailInfoById(Integer id);

    List<Article> getAllArticleDetail();

}
