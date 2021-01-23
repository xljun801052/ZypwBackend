package com.zypw.zypwauth.mapper;

import com.zypw.zypwcommon.entity.businessEntity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizeMapper {
    User findUserInfoByUserId(String userId);
}
