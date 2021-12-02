package com.xlys.zypwhomepage.service.impl;

import com.xlys.zypwhomepage.service.FavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Value("${app.cache.redis.comment-star.list-prefix}")
    private String COMMENT_STAR_LIST_CACHE_PREFIX;

    @Value("${app.cache.redis.comment-star.count-prefix}")
    private String COMMENT_STAR_COUNT_CACHE_PREFIX;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public Boolean changeStarStatus(Integer commentId, Integer userId, boolean favorited) {
        boolean performFlag = true;
        try {
            if (favorited) {
                redisTemplate.opsForSet().add(COMMENT_STAR_LIST_CACHE_PREFIX + commentId, userId);
                redisTemplate.opsForValue().increment(COMMENT_STAR_COUNT_CACHE_PREFIX + commentId);
                log.info("User:[{}] star for comment:[{}] successfully!", userId, commentId);
            } else {
                Long removedCount = redisTemplate.opsForSet().remove(COMMENT_STAR_LIST_CACHE_PREFIX + commentId, userId);
                redisTemplate.opsForValue().decrement(COMMENT_STAR_COUNT_CACHE_PREFIX + commentId);
                log.info("User:[{}] cancel star for comment:[{}] successfully!", userId, commentId);
            }
        } catch (Exception e) {
            performFlag = false;
            log.error("Error occurs when user:[{}] perform star action for comment:[{}]. Exception info:{}", userId, commentId, e.getMessage());
            e.printStackTrace();
        }
        return performFlag;
    }
}
