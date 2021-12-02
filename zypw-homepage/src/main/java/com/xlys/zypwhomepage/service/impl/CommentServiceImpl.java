package com.xlys.zypwhomepage.service.impl;

import com.xlys.zypwhomepage.domain.Comment;
import com.xlys.zypwhomepage.mapper.CommentMapper;
import com.xlys.zypwhomepage.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Override
    public List<HashMap<String, Object>> getAllArticleComments(Integer aid, Integer userId) {
        List<HashMap<String, Object>> commentList = commentMapper.getAllArticleComments(aid, userId);
        return commentList;
    }

    @Override
    @Transactional
    public Integer addNewComment(Comment comment) {
        commentMapper.addNewComment(comment);
        log.info("add new comment successfully! CommentId:[{}]", comment.getId());
        return comment.getId();
    }

    @Override
    public List<Comment> getSubCommentsByCommentId(Integer cid) {
        return commentMapper.getSubCommentsByCommentId(cid);
    }

    @Override
    @Transactional
    public Integer deleteCommentByCid(Integer cid) {
        Integer result = commentMapper.deleteCommentByCid(cid);
        log.info("delete comment done! Affected rows:[{}]", result);
        return result;
    }

    @Override
    public List<Map<String,Object>> getAllSubCommentsRecursivelyBySubCommentId(Integer scid) {
        return commentMapper.getAllSubCommentsRecursivelyBySubCommentId(scid);
    }
}
