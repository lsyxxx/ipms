package com.abt.wf.service;


import com.abt.wf.model.ValidationResult;
import com.abt.wf.entity.FlowOperationLog;
import org.camunda.bpm.engine.runtime.ProcessInstance;

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

    Map<String, Object> getVariableMap();

//    /**
//     * 获取流程图
//     * @param form  业务数据表单/对象
//     * @return List<ApprovalTask>
//     */
//    List<ApprovalTask> getPreview(T form);

    /**
     * 获取已进行操作记录
     * @param entityId 业务Id
     */
    List<FlowOperationLog> getCompletedOperationLogByEntityId(String entityId);
    //根据流程id获取Log
//    List<FlowOperationLog> getOperationLogByProcessId(String processId);

    //应该和getPreview(), getLog()保持一样的返回
    //如果不一样可以参考飞书作法，审批中的只显示已完成的记录
//    List<ApprovalTask> getFullProcessPath(T form);

    /**
     * 当前task完成后处理，比如抄送，保存log
     */
    void afterTask(T form);

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
    void preview(T form);

}
