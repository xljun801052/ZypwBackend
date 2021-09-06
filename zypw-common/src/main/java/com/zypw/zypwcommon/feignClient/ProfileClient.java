package com.zypw.zypwcommon.feignClient;

import com.zypw.zypwcommon.entity.businessEntity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@FeignClient(name = "zypw-profile-service")
public interface ProfileClient {

    /**
     * retrieve the account information by account name
     */
    @RequestMapping("/getAccountInfo/{username}")
    User getAccountInfo(@PathVariable("username") String username);


    @RequestMapping("/save")
    User saveUser(@RequestBody User user);
}
