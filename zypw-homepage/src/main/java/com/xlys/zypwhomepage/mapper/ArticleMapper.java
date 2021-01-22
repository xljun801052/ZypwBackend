package com.xlys.zypwhomepage.mapper;

import com.xlys.zypwhomepage.domain.Article;
import com.xlys.zypwhomepage.domain.Comment;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface ArticleMapper {
    Article getArticleDetailInfoById(Integer id);

    List<Article> getAllArticleDetail();

    List<HashMap<String, Object>> getAllArticleComments(Integer id);

}
