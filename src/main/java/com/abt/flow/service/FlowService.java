package com.abt.flow.service;

import com.abt.flow.model.FlowOperationLogVo;
import com.abt.flow.model.Form;
import com.abt.flow.model.ProcessVo;
import com.abt.sys.model.dto.UserView;

import java.io.InputStream;
import java.util.List;

/**
 * 流程处理
 */
public interface FlowService {

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
     * @param user 操作用户
     * @param vo 流程对象
     */
    void completeTask(UserView user, ProcessVo vo);

    /**
     * 获取流程操作记录
     * @param processInstanceId 流程实例ID
     * @return TODO: 返回类型
     */
    List<FlowOperationLogVo> getOperationLogs(String processInstanceId);


    /**
     * 获取流程图，高亮已经完成的任务
     * @param processInstanceId
     * @return
     */
    InputStream getHighLightedTaskPngDiagram(String processInstanceId);
}
