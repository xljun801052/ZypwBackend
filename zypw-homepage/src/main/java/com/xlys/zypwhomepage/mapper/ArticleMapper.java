package com.xlys.zypwhomepage.mapper;

import com.xlys.zypwhomepage.domain.Article;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleMapper {
    Article getArticleDetailInfoById(Integer aid, Integer userId);

    List<Article> getAllArticleDetail();

    List<Article> getCurrentPageArticlesAndTotalCount(Integer pageSize, Integer startIndex);

    Integer getTotalArticleCount();
}
