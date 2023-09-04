package com.abt.flow.service;

import com.abt.flow.model.Form;
import com.abt.flow.model.ProcessVo;
import com.abt.flow.model.entity.FlowOperationLog;
import com.abt.sys.model.dto.UserView;

import java.io.InputStream;
import java.util.List;

/**
 * 流程处理
 */
public interface FlowBaseService {

    /**
     * 启动一个流程，不包括完成申请Task
     *
     * @param bizType 业务类型Id
     * @param user    用户信息
     * @return ProcessInstance 流程实例
     */
    ProcessVo start(String bizType, UserView user, Form form);

    /**
     * 完成节点
     * 1. 用户认领任务
     * 2. 完成task
     * @param user 操作用户
     * @param vo 流程对象
     */
    void completeTask(UserView user, ProcessVo vo);

    /**
     * 获取流程操作记录
     *
     * @param processInstanceId 流程实例ID
     * @return TODO: 返回类型
     */
    List<FlowOperationLog> getOperationLogs(String processInstanceId);


    /**
     * 获取流程图，高亮已经完成的任务
     * @param processInstanceId
     * @return
     */
    InputStream getHighLightedTaskPngDiagram(String processInstanceId, String processDefinitionId);

    /**
     * 删除一个流程
     * @param processInstanceId
     */
    void deleteProcess(String processInstanceId);

    /**
     * 撤销一个流程
     * TODO: 撤销条件
     * @param processInstanceId
     */
    void cancelProcess(String processInstanceId);
}
