package com.zypw.zypwprofile.service.impl;

import com.zypw.zypwcommon.entity.businessEntity.User;
import com.zypw.zypwprofile.repository.AccountInfoRepository;
import com.zypw.zypwprofile.service.AccountInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountInfoInfoServiceImpl implements AccountInfoService {

    @Autowired
    private AccountInfoRepository accountInfoRepository;

    @Override
    public User findByUsername(String username) {
        return accountInfoRepository.findByUsername(username);
    }

    @Override
    public User save(User user) {
        User savedUser = accountInfoRepository.save(user);
        return savedUser;
    }
}
