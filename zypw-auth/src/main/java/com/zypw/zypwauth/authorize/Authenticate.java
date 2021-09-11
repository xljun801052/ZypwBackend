package com.zypw.zypwauth.authorize;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zypw.zypwcommon.entity.authEntity.AuthUser;
import com.zypw.zypwauth.mapper.AuthorizeMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class Authenticate {

    @Resource
    private AuthorizeMapper authorizeMapper;

    @Resource
    private ObjectMapper objectMapper;

    @RequestMapping("/getAuthUser/{userAccount}")
    public AuthUser getAuthenticationUser(@PathVariable("userAccount") String userAccount) {
        log.info("Auth-Service get the request! Param:{}", userAccount);
        return authorizeMapper.findUserInfoByUserAccount(userAccount);
    }

    @SneakyThrows
    @RequestMapping("/save")
    public AuthUser saveOrUpdateAuthUser(String authUserInfo) {
        AuthUser authUser = objectMapper.readValue(authUserInfo, AuthUser.class);
        return authorizeMapper.save(authUser);
    }
}
