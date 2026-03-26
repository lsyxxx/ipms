package com.abt.wxapp.user.invoice.service;

import com.abt.wxapp.user.invoice.entity.OpenUserInvTitle;
import java.util.List;

/**
 * 发票抬头业务接口
 */
public interface InvTitleService {

    /**
     * 保存或更新发票抬头
     */
    OpenUserInvTitle saveInvTitle(OpenUserInvTitle invTitle);

    /**
     * 获取用户的发票抬头列表
     */
    List<OpenUserInvTitle> findListByUserId(String userId);

    /**
     * 删除发票抬头
     */
    void deleteInvTitle(String id);

    /**
     * 设置默认发票抬头
     */
    void setDefaultInvTitle(String userId, String id);

    /**
     * 根据 ID 获取单个发票抬头详情
     */
    OpenUserInvTitle findTitleById(String id);
}