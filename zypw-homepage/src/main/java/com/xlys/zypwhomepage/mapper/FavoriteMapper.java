package com.xlys.zypwhomepage.mapper;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface FavoriteMapper {
    Map<String, String> selectFavoriteStatus(String commentId, Integer userId);

    void changeFavorite(String commentId, Integer userId);
}
