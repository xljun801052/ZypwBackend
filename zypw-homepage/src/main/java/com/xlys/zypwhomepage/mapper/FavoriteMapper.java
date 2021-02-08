package com.xlys.zypwhomepage.mapper;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface FavoriteMapper {

    /**查询点赞记录情况**/
    Map<String, Integer> selectFavoriteStatus(Integer commentId, Integer userId);

    /**修改点赞状态**/
    // TODO: 2021-02-08 如果多个参数且类型不一致，除了map形式传参，还可以有其他方式吗？对应的parameter 应该传什么类型呢？
    // TODO: 2021-02-08 如果多个参数且类型一致，除了map形式传参，还可以有其他方式吗？对应的parameter 应该传什么类型呢？
    int changeFavorite(Integer commentId, Integer userId,Integer favoriteStatus);

    /**新增点赞记录**/
    void addNewFavorite(Integer commentId, Integer userId);
}
