package com.abt.wxapp.user.invoice.service;

import com.abt.wxapp.user.invoice.model.InvTitleRequest;
import com.abt.wxapp.user.invoice.model.InvTitleVo;

import java.util.List;

/**
 * 发票抬头业务接口
 */
public interface InvTitleService {

    /**
     * 保存或更新发票抬头
     */
    InvTitleVo save(InvTitleRequest request);

    /**
     * 获取用户的发票抬头列表
     */
    List<InvTitleVo> findList(String userId);

    /**
     * 删除发票抬头
     */
    void delete(String id);

    /**
     * 设置默认发票抬头
     */
    void updateDefault(String userId, String id);

    /**
     * 根据 ID 获取单个发票抬头详情
     */
    InvTitleVo findById(String id);
}