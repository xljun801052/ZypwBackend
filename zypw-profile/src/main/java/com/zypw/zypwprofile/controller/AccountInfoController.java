package com.zypw.zypwprofile.controller;

import com.zypw.zypwcommon.entity.businessEntity.User;
import com.zypw.zypwprofile.service.AccountInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
@Slf4j
public class AccountInfoController {

    @Resource
    private AccountInfoService accountInfoService;

    @RequestMapping("/getAccountInfo/{username}")
    public User getAccountInfo(@PathVariable String username) {
        return accountInfoService.findByUsername(username);
    }

    @RequestMapping("/save")
    public User saveUser(@RequestBody User user) {
        return accountInfoService.save(user);
    }
}
