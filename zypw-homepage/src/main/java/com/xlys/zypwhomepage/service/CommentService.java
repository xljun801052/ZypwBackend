package com.xlys.zypwhomepage.service;

import com.xlys.zypwhomepage.domain.Comment;

import java.util.List;

public interface CommentService {
    Integer addNewComment(Comment comment);

    List<Comment> getSubCommentsByCommentId(Integer cid);

    Integer deleteCommentByCid(Integer cid);
}
