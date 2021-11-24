package com.xlys.zypwhomepage.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xlys.zypwhomepage.domain.Comment;
import com.xlys.zypwhomepage.service.CommentService;
import com.zypw.zypwcommon.entity.responseEntity.AxiosResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CommentService commentService;

    /**
     * get sub comments info by parent commentId
     */
    @PostMapping("/subComments")
    public String getSubCommentsByCommentId(@RequestBody JSONObject jsonObject) {
        Integer cid = Integer.parseInt(jsonObject.get("parentId").toString());
        List<Comment> subCommentList = commentService.getSubCommentsByCommentId(cid);
        System.out.println("subCommentList = " + subCommentList);
        return JSONObject.toJSONString(subCommentList, SerializerFeature.WriteNullNumberAsZero);
    }

    /**
     * add a new comment
     */
    @SneakyThrows
    @PostMapping("/add")
    public AxiosResult addNewComment(@RequestBody JSONObject jsonObject) {
        Comment comment = mapper.readValue(jsonObject.toJSONString(), Comment.class);
        Integer result = commentService.addNewComment(comment);
        String message = Integer.signum(result)==1?"success":"error";
        return new AxiosResult(200, message);
    }

    @PostMapping("/delete/{cid}")
    public AxiosResult deleteComment(@PathVariable("cid") Integer cid) {
        Integer result = commentService.deleteCommentByCid(cid);
        String message = Integer.signum(result)==1?"success":"error";
        return new AxiosResult(201, message);
    }
}
