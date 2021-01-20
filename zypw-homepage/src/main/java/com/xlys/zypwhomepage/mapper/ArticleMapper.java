package com.xlys.zypwhomepage.mapper;

import com.xlys.zypwhomepage.domain.Article;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleMapper {
    Article getArticleDetailInfoById(Integer id);

    List<Article> getAllArticleDetail();

}
