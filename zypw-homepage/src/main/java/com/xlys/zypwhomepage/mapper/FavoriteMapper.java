package com.xlys.zypwhomepage.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public interface FavoriteMapper {
    Map<String, Integer> selectFavoriteStatus(Integer commentId, Integer userId);

    void changeFavorite(Integer commentId, Integer userId, boolean favoriteStatus);

    Integer addNewFavorite(Integer commentId, Integer userId);


    List<Map<String, Integer>> getStarInfoByCid(Integer cid);

    Integer addNewFavorites(Integer cid, Set<Integer> increasedUserIds);

    Integer batchUpdateFavorite(Integer cid, List<Map<String, Integer>> dbStarInfo);
}
