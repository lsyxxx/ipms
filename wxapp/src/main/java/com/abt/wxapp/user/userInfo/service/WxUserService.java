package com.abt.wxapp.user.userInfo.service;

import com.abt.wxapp.user.userInfo.model.WxUserSaveRequest;

public interface WxUserService {

    /**
     * 编辑/保存客户信息
     */
    void save(WxUserSaveRequest request);
}