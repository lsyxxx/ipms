package com.abt.wxapp.user.userInfo.service;

import com.abt.wxapp.user.userInfo.entity.OpenUserInfo;
import com.abt.wxapp.user.userInfo.model.OpenUserInfoRequestForm;
import org.springframework.data.domain.Page;

public interface OpenUserService {

    /**
     * 分页多条件查询用户信息
     */
    Page<OpenUserInfo> findByQuery(OpenUserInfoRequestForm requestForm);

    /**
     * 编辑/保存客户信息
     */
    void saveUser(OpenUserInfo openUserInfo);
}