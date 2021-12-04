package com.xlys.zypwhomepage.mapper;

import com.xlys.zypwhomepage.domain.Comment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CommentMapper {

    List<HashMap<String, Object>> getAllArticleComments(Integer aid, Integer userId);

    Integer addNewComment(Comment comment);

    List<Comment> getSubCommentsByCommentId(Integer cid);

    Integer deleteCommentByCid(Integer cid);

    List<Map<String,Object>> getAllSubCommentsRecursivelyBySubCommentId(Integer scid);

    Set<Integer> getAllValidCommentIds();
}
