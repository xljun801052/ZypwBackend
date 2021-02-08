package com.xlys.zypwhomepage.service.impl;

import com.xlys.zypwhomepage.mapper.FavoriteMapper;
import com.xlys.zypwhomepage.service.FavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public Boolean changeFavorite(String commentId, Integer userId) {
        // 先查询点赞状态。如果没有点赞过则新增点赞记录，如果已经点赞过，变更点赞信息
        Map<String, Integer> queryResult = favoriteMapper.selectFavoriteStatus(Integer.parseInt(commentId), userId);
        // 有过点赞记录
        if (queryResult.get("isAlreadyFavorite") == 0 || queryResult.get("isAlreadyFavorite") == 1) {
            // 点赞了，将已点赞状态改为取消点赞
            // 取消点赞了，将取消点赞状态改为恢复点赞
            int updatedRows = favoriteMapper.changeFavorite(Integer.parseInt(commentId), userId,queryResult.get("isAlreadyFavorite"));
            return updatedRows > 0 ? true : false;
        }
        // 没有点赞记录
        else if (queryResult.get("isAlreadyFavorite") == 2) {
            // 进行新增点赞数据插入
            try {
                favoriteMapper.addNewFavorite(Integer.parseInt(commentId), userId);
                return true;
            } catch (Exception e) {
                log.error("插入点赞记录异常");
                e.printStackTrace();
                return false;
            }
        }
        return null;
    }
}
