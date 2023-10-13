package com.abt.flow.service;

import com.abt.flow.model.FlowForm;

/**
 * 流程业务统一入口
 * 每个流程业务需要实现
 */
public interface FlowEntry<T extends FlowForm> {

    /**
     * 获取流程实例详情
     * @param procId 流程实例id
     */
    T get(String procId);

}
