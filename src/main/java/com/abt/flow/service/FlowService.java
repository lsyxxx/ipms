package com.abt.flow.service;

import com.abt.flow.model.ReimburseApplyForm;
import com.abt.sys.model.dto.UserView;
import org.flowable.engine.runtime.ProcessInstance;

import java.io.InputStream;
import java.util.List;

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
    ProcessInstance start(String bizType, UserView user, ReimburseApplyForm form);

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


    /**
     * 获取流程操作记录
     * @param processInstanceId 流程实例ID
     * @return TODO: 返回类型
     */
    List getOperationLogs(String processInstanceId);


    /**
     * 获取流程图，高亮已经完成的任务
     * @param processInstanceI
     * @return
     */
    InputStream getPngDiagramWithHighLightedTask(String processInstanceI);

}
