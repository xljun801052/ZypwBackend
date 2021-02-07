package com.xlys.zypwhomepage.mapper;

import com.xlys.zypwhomepage.domain.Article;
import com.xlys.zypwhomepage.domain.Comment;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface ArticleMapper {
    Article getArticleDetailInfoById(Integer aid, Integer userId);

    List<Article> getAllArticleDetail();

    List<HashMap<String, Object>> getAllArticleComments(Integer aid,Integer userId);

    List<Comment> getSubCommentsByCommentId(Integer cid);
}
