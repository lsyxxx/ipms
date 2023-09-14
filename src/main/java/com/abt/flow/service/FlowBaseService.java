package com.abt.flow.service;

import com.abt.flow.model.FlowForm;
import com.abt.flow.model.ProcessVo;
import com.abt.flow.model.ReimburseApplyForm;
import com.abt.flow.model.entity.FlowOperationLog;
import com.abt.sys.model.dto.UserView;
import org.flowable.task.api.Task;

import java.io.InputStream;
import java.util.List;

/**
 * 流程处理
 */
public interface FlowBaseService<T extends FlowForm> {

    /**
     * 正常完成节点
     * 1. 完成task
     * 2. 指定下一个任务assignee;
     * @param user 操作用户
     * @param vo 流程对象，传入时必须：processInstanceId流程实例id, nextAssignee下一个节点执行人
     */
    void completeTask(UserView user, ProcessVo<T> vo);

    /**
     * 任务驳回
     * 1. 审批意见
     * 2. 流程终止
     * @param user 审批用户
     * @param vo 流程vo
     */
    void rejectTask(UserView user, ProcessVo<T> vo);

    /**
     * 获取流程操作记录
     * @param processInstanceId 流程实例ID
     * @return FlowOperationLog
     */
    List<FlowOperationLog> getOperationLogs(String processInstanceId);


    /**
     * 获取流程图，高亮已经完成的任务
     * @param processInstanceId
     * @return
     */
    InputStream getHighLightedTaskPngDiagram(String processInstanceId, String processDefinitionId);

    /**
     * 删除一个正在进行的流程
     */
    void deleteProcess(String processInstanceId, String delReason);

    /**
     * 撤销一个流程
     * TODO: 撤销条件
     * @param processInstanceId
     */
    void cancelProcess(String processInstanceId);


    /**
     * 根据流程实例ID获取正在进行的task
     * @param processInstanceId 流程实例ID
     * @return 如果没有结果则返回NULL
     */
    Task getActiveTask(String processInstanceId);

    /**
     * 一般审批，只做2点
     * 1. 同意/拒绝
     * 2. 评论
     * 3. 同意则进行下一个节点，拒绝则终止流程
     */
    ProcessVo<T> check(UserView user, ProcessVo<T> vo);


    /**
     * 启动流程
     * @param user 启动用户
     */
    ProcessVo<T> start(UserView user, ProcessVo<T> vo);
}
