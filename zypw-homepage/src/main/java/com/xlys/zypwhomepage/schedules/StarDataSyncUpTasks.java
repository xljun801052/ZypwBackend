package com.xlys.zypwhomepage.schedules;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StarDataSyncUpTasks {

    @Scheduled(cron = "0 0 1 * * ? ")
    public void syncUpCommentDataFromCache2MySQL() {
        log.info("syncUpCommentDataFromCache2MySQL task execution start!");
        // TODO: 2021/12/2 进行缓存数据同步操作
        log.info("syncUpCommentDataFromCache2MySQL task execution finished!");
    }
}
