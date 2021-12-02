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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CommentService commentService;

    /**
     * 获取所有文章评论详情
     */
    @RequestMapping("/historical/all")
    public AxiosResult getAllArticleComments(@RequestBody JSONObject jsonObject) {
        Integer userId = (Integer) jsonObject.get("userId");
        Integer aid = (Integer) jsonObject.get("aId");
        List<HashMap<String, Object>> commentInfo = commentService.getAllArticleComments(aid, userId);
        return new AxiosResult(200, "success", JSONObject.toJSONString(commentInfo, SerializerFeature.WriteNullNumberAsZero));
    }

    /**
     * get sub comments info by top layer parent commentId。
     */
    @PostMapping("/subComments")
    public String getSubCommentsByCommentId(@RequestBody JSONObject jsonObject) {
        Integer cid = Integer.parseInt(jsonObject.get("parentId").toString());
        List<Comment> subCommentList = commentService.getSubCommentsByCommentId(cid);
        System.out.println("subCommentList = " + subCommentList);
        return JSONObject.toJSONString(subCommentList, SerializerFeature.WriteNullNumberAsZero);
    }


    @PostMapping("allLayerSubComments")
    // TODO: 2021/11/27 RequestParam请求参数解析的流程？为啥用@RequestParam就不可以了？
    public AxiosResult getAllSubCommentsRecursivelyBySubCommentId(@RequestBody JSONObject jsonObject) {
        Integer scid = Integer.parseInt(jsonObject.get("scid").toString());
        List<Map<String, Object>> allLayerSubComments = commentService.getAllSubCommentsRecursivelyBySubCommentId(scid);
        return new AxiosResult(200, "success", JSONObject.toJSONString(allLayerSubComments, SerializerFeature.WriteNullNumberAsZero));
    }

    /**
     * add a new comment
     */
    @SneakyThrows
    @PostMapping("/add")
    public AxiosResult addNewComment(@RequestBody JSONObject jsonObject) {
        Comment comment = mapper.readValue(jsonObject.toJSONString(), Comment.class);
        Integer commentId = commentService.addNewComment(comment);
        String message = Integer.signum(commentId) == 1 ? "success" : "error";
        return new AxiosResult(200, message, new JSONObject(new HashMap<String, Object>() {{
            put("commentId", commentId);
        }}));
    }

    @PostMapping("/delete/{cid}")
    public AxiosResult deleteComment(@PathVariable("cid") Integer cid) {
        Integer result = commentService.deleteCommentByCid(cid);
        String message = Integer.signum(result) == 1 ? "success" : "error";
        return new AxiosResult(201, message);
    }
}
