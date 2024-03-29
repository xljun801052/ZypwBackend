package com.xlys.zypwhomepage.service;

import com.xlys.zypwhomepage.domain.Comment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface CommentService {
    Integer addNewComment(Comment comment);

    List<Comment> getSubCommentsByCommentId(Integer cid);

    Integer deleteCommentByCid(Integer cid);

    List<Map<String,Object>> getAllSubCommentsRecursivelyBySubCommentId(Integer scid);

    List<HashMap<String, Object>> getAllArticleComments(Integer aid, Integer userId);
}
