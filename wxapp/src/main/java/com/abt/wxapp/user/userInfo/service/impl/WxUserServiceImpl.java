package com.abt.wxapp.user.userInfo.service.impl;

import com.abt.openuser.entity.OpenUserInfo;
import com.abt.openuser.repository.OpenUserRepository;
import com.abt.wxapp.user.userInfo.model.OpenUserInfoRequestForm;
import com.abt.wxapp.user.userInfo.service.WxUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WxUserServiceImpl implements WxUserService {

    private final OpenUserRepository openUserInfoRepository;


    @Override
    public void saveUser(OpenUserInfo openUserInfo) {
        openUserInfoRepository.save(openUserInfo);
    }


    /**
     * TODO: 微信直接注册
     */
    public void registerByWx() {

    }

    /**
     * TODO: 手机号注册
     */
    public void registerByPhone() {

    }
}