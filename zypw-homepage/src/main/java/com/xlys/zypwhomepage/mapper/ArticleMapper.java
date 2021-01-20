package com.xlys.zypwhomepage.mapper;

import com.xlys.zypwhomepage.domain.Article;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleMapper {
    Article getArticleDetailInfoById(Integer id);
}
