package com.abt.wxapp.user.userInfo.service;

import com.abt.openuser.entity.OpenUserInfo;
import com.abt.wxapp.user.userInfo.model.OpenUserInfoRequestForm;
import org.springframework.data.domain.Page;

public interface WxUserService {

    /**
     * 编辑/保存客户信息
     */
    void saveUser(OpenUserInfo openUserInfo);
}