package com.xlys.zypwhomepage.service;

import com.xlys.zypwhomepage.domain.Article;
import com.xlys.zypwhomepage.domain.Comment;

import java.util.HashMap;
import java.util.List;

public interface ArticleService {
    Article getArticleDetailInfoById(Integer aid,Integer userId);

    List<Article> getAllArticleDetail();

    List<HashMap<String, Object>> getAllArticleComments(Integer id,Integer userId);

    List<Comment> getSubCommentsByCommentId(Integer cid);
}
