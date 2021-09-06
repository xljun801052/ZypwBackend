package com.zypw.zypwprofile.repository;

import com.zypw.zypwcommon.entity.businessEntity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountInfoRepository extends CrudRepository<User, Integer> {

    User findByUsername(String username);

}
