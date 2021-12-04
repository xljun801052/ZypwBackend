package com.xlys.zypwhomepage.schedules;

import com.xlys.zypwhomepage.mapper.CommentMapper;
import com.xlys.zypwhomepage.mapper.FavoriteMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
@Slf4j
public class StarDataSyncUpTasks {

    @Value("${app.cache.redis.comment-star.list-prefix}")
    private String COMMENT_STAR_LIST_CACHE_PREFIX;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private CommentMapper commentMapper;

//    @Scheduled(cron = "0 0 0/2 * * ? ")
    @Scheduled(cron = "0 20 11 * * ? ")
    public void syncUpCommentDataFromCache2MySQL() {
        log.info("> > > syncUpCommentDataFromCache2MySQL task execution start!");
        Set<Integer> allCids = commentMapper.getAllValidCommentIds();
        for (Integer cid : allCids) {
            Long valueCount = null;
            try {
                valueCount = redisTemplate.opsForSet().size(COMMENT_STAR_LIST_CACHE_PREFIX + cid);
                log.info("Current comment star list count:[{}]", valueCount);
                if (valueCount == null || valueCount == 0) {
                    log.info("No need for sync up star data of comment:[{}]", cid);
                    continue;
                }
            } catch (Exception e) {
                log.error("Error occurs when get star data size for comment:[{}]. The exception message is:{}", cid, e.getMessage());
                e.printStackTrace();
            }
            try {
                log.info("****** Start sync up action for comment:[{}] ******", cid);
                Set<Object> cacheStarInfo = redisTemplate.opsForSet().members(COMMENT_STAR_LIST_CACHE_PREFIX + cid);
                List<Map<String, Integer>> dbStarInfo = favoriteMapper.getStarInfoByCid(cid);
                Set<Integer> dbStarUserIds = dbStarInfo.stream().map(m -> m.get("userId")).collect(toSet());

                assert cacheStarInfo != null;
                Set<Integer> increasedUserIds = cacheStarInfo.stream()
                        .filter( s -> !dbStarUserIds.contains(s))
                        .map(s->(Integer)s)
                        .collect(toSet());
                for (Map<String, Integer> cStarMap : dbStarInfo) {
                    if (cacheStarInfo.contains(cStarMap.get("userId"))) {
                        cStarMap.put("favorited", 0);
                    } else {
                        cStarMap.put("favorited", 1);
                    }
                }
                if (increasedUserIds.isEmpty()) {
                    log.info("No need to isert new added star info since the increment count is zero.");
                } else {
                    Integer increCount = favoriteMapper.addNewFavorites(cid, increasedUserIds);
                    log.info("Increment star count:[{}]", increCount);
                }
                Integer updateCount = favoriteMapper.batchUpdateFavorite(cid, dbStarInfo);
                log.info("Update star count:[{}]", updateCount);
                log.info("****** Finished sync up action for comment:[{}] ****** ", cid);
            } catch (Exception e) {
                log.error("Error occurs when sync up star data for comment:[{}]. The exception message is:{}", cid, e.getMessage());
                e.printStackTrace();
            }

        }
        log.info("> > > syncUpCommentDataFromCache2MySQL task execution finished!");
    }
}
