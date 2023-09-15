package com.abt.flow.model;

import com.abt.common.model.AuditInfo;
import com.abt.common.util.TimeUtil;
import lombok.Data;
import org.flowable.engine.history.HistoricProcessInstance;

import java.time.LocalDateTime;

/**
 * 流程信息VO
 * 用于传递前端查看流程列表(待办/已办列表)
 */
@Data
public class FlowInfoVo extends AuditInfo {


    /**
     * 流程实例id
     */
    private String procInstId;
    /**
     * 业务数据表id
     */
    private String businessKey;
    /**
     * 流程类型代码
     */
    private String flowId;
    private String flowCode;
    private String flowName;
    /**
     * 正在进行的节点
     */
    private String activityName;
    /**
     * 流程实例描述
     */
    private String description;

    /**
     * 审批状态
     */
    private String state;

    /**
     * 审批结果
     */
    private String result;

    /**
     * 历史审批人;
     * 多个用逗号隔开
     */
    private String invokers;

    /**
     * 当前负责人
     */
    private String currentUser;
}
