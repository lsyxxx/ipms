package com.abt.flow.service;

import com.abt.flow.model.Decision;
import com.abt.flow.model.entity.FlowOperationLog;
import com.abt.sys.model.dto.UserView;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 流程处理
 * 与业务对象相关的，放在具体的业务子类处理
 */
public interface FlowBaseService {

    /**
     * 正常完成节点
     */
    void completeTask(String taskId);
    /**
     * 任务驳回
     * 流程终止
     */
    void rejectTask(UserView user, String procId);

    /**
     * 获取流程操作记录
     * @param processInstanceId 流程实例ID
     * @return FlowOperationLog
     */
    List<FlowOperationLog> getOperationLogs(String processInstanceId);


    /**
     * 获取流程图，高亮已经完成的任务
     */
    InputStream getHighLightedTaskPngDiagram(String processInstanceId, String processDefinitionId);

    /**
     * 删除一个正在进行的流程
     */
    void deleteRunningProcess(String processInstanceId, String delReason, UserView user);

    /**
     * 撤销一个正在进行的流程
     */
    void cancelRunningProcess(String processInstanceId, UserView user);


    /**
     * 根据流程实例ID获取正在进行的task
     * @param processInstanceId 流程实例ID
     * @return 如果没有结果则返回抛出异常
     */
    Task getActiveTask(String processInstanceId, String errMsg);

    /**
     * 一般审批，只处理同意、拒绝两种结果
     * 1. 同意/拒绝
     * 2. 同意则进行下一个节点，拒绝则终止流程
     */
    void check(UserView user, Decision result, String procId, String taskId);

    /**
     * 启动流程
     * @param user 启动用户
     * @return ProcessInstance 流程实例
     */
    ProcessInstance start(UserView user, String procDefId, String businessKey, Map<String, Object> variblesMap);

    /**
     * 保存businessKey=审批结果,审批状态
     * @param result 审批结果
     * @param state 审批状态
     */
    String businessKey(String result, String state);
}
