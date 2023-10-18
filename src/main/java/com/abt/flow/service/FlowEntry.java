package com.abt.flow.service;

import com.abt.flow.model.ApplyForm;
import com.abt.flow.model.FlowForm;
import com.abt.flow.model.entity.Reimburse;
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

    void apply(ApplyForm<Map<String, Object>> applyForm, UserView user) throws JsonProcessingException;

    /**
     * 将参数map转为对应类
     */
    T convert(Map<String, Object> map) throws JsonProcessingException;

}
