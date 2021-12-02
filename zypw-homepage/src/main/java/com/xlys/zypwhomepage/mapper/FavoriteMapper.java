package com.xlys.zypwhomepage.mapper;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface FavoriteMapper {
    Map<String, Integer> selectFavoriteStatus(Integer commentId, Integer userId);

    void changeFavorite(Integer commentId, Integer userId, boolean favoriteStatus);

    Integer addNewFavorite(Integer commentId, Integer userId);
}
