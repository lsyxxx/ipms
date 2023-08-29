package com.abt.flow.service;

import com.abt.sys.model.dto.UserView;
import org.flowable.engine.runtime.ProcessInstance;

/**
 * 流程处理
 */
public interface FlowService {

    /**
     * 启动一个流程
     * @param bizType 业务类型Id
     * @param user  用户信息
     * @return ProcessInstance 流程实例
     */
    ProcessInstance start(String bizType, UserView user);

    /**
     * 完成当前任务
     * @param taskId 任务id
     */
    void completeTask(String taskId);

    /**
     * 完成流程后处理。流程最后一个任务完成后流程自动结束
     * TODO: 流程结束后处理
     */
    void afterCompleted();

}
