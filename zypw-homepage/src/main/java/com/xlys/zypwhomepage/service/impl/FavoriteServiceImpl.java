package com.xlys.zypwhomepage.service.impl;

import com.xlys.zypwhomepage.mapper.FavoriteMapper;
import com.xlys.zypwhomepage.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public Boolean changeFavorite(String commentId, Integer userId) {
        // 先查询点赞状态。如果没有点赞过则新增点赞记录，如果已经点赞过，变更点赞信息
        Map<String, String> queryResult = favoriteMapper.selectFavoriteStatus(commentId, userId);
        if (queryResult.get("isAlreadyFavorite").equalsIgnoreCase("")) {
            favoriteMapper.changeFavorite(commentId, userId);
        } else if (queryResult.get("isAlreadyFavorite").equalsIgnoreCase("true")) {
            return null;
        }
            return null;
    }
}
