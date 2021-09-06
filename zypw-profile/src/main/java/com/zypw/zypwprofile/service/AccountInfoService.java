package com.zypw.zypwprofile.service;

import com.zypw.zypwcommon.entity.businessEntity.User;

public interface AccountInfoService {

    User findByUsername(String username);

    User save(User user);
}
