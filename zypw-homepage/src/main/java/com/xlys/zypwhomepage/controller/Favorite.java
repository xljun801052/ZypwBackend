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
public class Favorite {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 点赞状态变更
     */
    @RequestMapping("change")
    public String changeFavorite(@RequestBody JSONObject jsonObject) {
        String commentId = (String) jsonObject.get("cid");
        Integer userId = (Integer) jsonObject.get("userId");
        Boolean changeResult = favoriteService.changeFavorite(commentId, userId);
        if (changeResult) {
            return JSONObject.toJSONString(new AxiosResult(200, "点赞状态变成成功", changeResult));
        } else {
            return JSONObject.toJSONString(new AxiosResult(500, "点赞状态变成失败", changeResult));
        }
    }
}
