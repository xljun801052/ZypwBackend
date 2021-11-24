package com.xlys.zypwhomepage.mapper;

import com.xlys.zypwhomepage.domain.Comment;

import java.util.List;

public interface CommentMapper {
    Integer addNewComment(Comment comment);

    List<Comment> getSubCommentsByCommentId(Integer cid);

    Integer deleteCommentByCid(Integer cid);
}
