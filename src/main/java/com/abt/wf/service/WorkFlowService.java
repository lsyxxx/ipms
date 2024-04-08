package com.abt.wf.service;


import com.abt.wf.model.ReimburseForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.entity.FlowOperationLog;
import org.camunda.bpm.engine.task.Task;

import java.util.List;
import java.util.Map;

/**
 * 流程处理基本接口
 * @param <T> 业务表单/对象
 */
public interface WorkFlowService<T> {

    /**
     * 生成流程参数
     * @param form 业务数据表单/对象
     * @return Map<String, Object>: camunda流程参数要求
     */
    Map<String, Object> createVariableMap(T form);

    /**
     * 生成流程中businessKey;
     * @param form 业务数据
     */
    String businessKey(T form);

    /**
     * 获取已进行操作记录
     * @param entityId 业务Id
     */
    List<FlowOperationLog> getCompletedOperationLogByEntityId(String entityId);

    /**
     * 申请流程
     */
    void apply(T form);

    /**
     * 审批
     */
    void approve(T form);

    void setAuthUser(String userid);

    void clearAuthUser();

    /**
     * 撤销
     * @param entityId 业务实体id
     */
    void revoke(String entityId);

    /**
     * 删除流程
     * @param entityId 业务实体id
     */
    void delete(String entityId);

    /**
     * 预览流程
     */
    List<UserTaskDTO> preview(T form);

    /**
     * 验证form中实体id
     * @param form 表单
     */
    void ensureEntityId(T form);

    /**
     * 审批前操作
     */
    Task beforeApprove(T baseForm, String authUser, String decision);

    /**
     * 登录用户是否是当前task的审批用户
     * @param form 表单，包含必须数据
     */
    boolean isApproveUser(T form);

    /**
     * 通知消息url
     * @param id
     */
    String notifyLink(String id);

    /**
     * 流程记录，包含已完成的和正在进行的
     */
    List<FlowOperationLog> processRecord(String entityId, String serviceName);
}
