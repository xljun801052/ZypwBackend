package com.zypw.zypwauth.mapper;

import com.zypw.zypwcommon.entity.authEntity.AuthUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizeMapper extends CrudRepository<AuthUser, Integer> {

    AuthUser findUserInfoByUserAccount(String userAccount);

}
