package com.abt.flow.service;

import com.abt.flow.model.ApplyForm;
import com.abt.sys.model.dto.UserView;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

/**
 * 流程业务统一入口
 * 每个流程业务需要实现
 */
public interface FlowEntry<T>{

    /**
     * 获取流程实例详情
     * @param procId 流程实例id
     * @return ApplyForm
     */
    ApplyForm<T> get(String procId);


    /**
     * 申请流程业务
     * @param user 申请用户
     * @param applyForm 申请表单
     */
    void apply(ApplyForm<T> applyForm, UserView user);

}
