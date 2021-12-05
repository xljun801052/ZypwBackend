package com.xlys.zypwhomepage.controller;


import com.alibaba.fastjson.JSONObject;
import com.xlys.zypwhomepage.service.FavoriteService;
import com.zypw.zypwcommon.entity.responseEntity.AxiosResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 点赞相关功能入口
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 评论点赞
     */
    @RequestMapping("/comment-star/act")
    public AxiosResult performStarCommentAct(@RequestBody JSONObject jsonObject) {
        Integer commentId = (Integer) jsonObject.get("cId");
        Integer userId = (Integer) jsonObject.get("userId");
        boolean favorited = (boolean) jsonObject.get("favorited");
        Boolean changeResult = favoriteService.changeStarStatus(commentId, userId, favorited);
        return new AxiosResult(200, "success", JSONObject.toJSONString(changeResult));
    }
}
