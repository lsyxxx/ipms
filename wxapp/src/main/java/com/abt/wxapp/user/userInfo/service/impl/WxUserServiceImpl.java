package com.abt.wxapp.user.userInfo.service.impl;

import com.abt.openuser.entity.OpenUserInfo;
import com.abt.openuser.repository.OpenUserRepository;
import com.abt.wxapp.user.userInfo.model.WxUserSaveRequest;
import com.abt.wxapp.user.userInfo.service.WxUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WxUserServiceImpl implements WxUserService {

    private final OpenUserRepository openUserInfoRepository;


    @Override
    @Transactional
    public void save(WxUserSaveRequest request) {
        OpenUserInfo openUserInfo = new OpenUserInfo();
        openUserInfo.setId(request.getId());
        openUserInfo.setName(request.getName());
        openUserInfo.setChannel(request.getChannel());
        openUserInfo.setOpenId(request.getOpenId());
        openUserInfo.setPhone(request.getPhone());
        openUserInfo.setAddress(request.getAddress());
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