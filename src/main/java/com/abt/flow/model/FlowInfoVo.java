package com.abt.flow.model;

import com.abt.common.model.AuditInfo;
import com.abt.common.util.TimeUtil;
import com.abt.flow.config.FlowableConstant;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * flowable中保存的: 审批结果,审批状态
     */
    private String businessStatus;



    public static FlowInfoVo create(HistoricProcessInstance process) {
        FlowInfoVo vo = new FlowInfoVo();
        vo.setProcInstId(process.getId());
        vo.setBusinessKey(process.getBusinessKey());
        vo.setCreateDate(TimeUtil.from(process.getStartTime()));
        vo.setCreateUserid(process.getStartUserId());


        String bizState = process.getBusinessStatus();
        vo.updateStateAndResult(bizState);

        return vo;
    }

    public FlowInfoVo updateStateAndResult(String businessStatus) {
        this.setBusinessStatus(businessStatus);
        this.setState(auditState(businessStatus));
        this.setResult(auditResult(businessStatus));
        return this;
    }

    public static FlowInfoVo create(ProcessInstance process) {
        FlowInfoVo vo = new FlowInfoVo();
        vo.setProcInstId(process.getId());
        vo.setBusinessKey(process.getBusinessKey());
        vo.setCreateDate(TimeUtil.from(process.getStartTime()));
        vo.setCreateUserid(process.getStartUserId());

        String bizState = process.getBusinessStatus();
        vo.setState(auditState(bizState));


        return vo;
    }

    /**
     * businessStatus = 审批结果，审批状态
     */
    public static String auditState(String businessStatus) {
        if (StringUtils.isBlank(businessStatus)) {
            return "";
        }
        return businessStatus.split(",")[0];
    }

    public static String auditResult(String businessStatus) {
        if (StringUtils.isBlank(businessStatus)) {
            return "";
        }
        return businessStatus.split(",")[1];
    }

    public FlowInfoVo updateTask(Task task) {
        if (task != null) {
            setCurrentUser(task.getAssignee());
            setActivityName(task.getName());
        }
        return this;
    }


    public FlowInfoVo updateVariables(List<HistoricVariableInstance> list) {
        Map<String, HistoricVariableInstance> varMap = new HashMap<>();
        if (list != null) {
            varMap = list.stream().collect(Collectors.toMap(HistoricVariableInstance::getVariableName, h -> h));
        }

        setFlowId(emptyIfNull(varMap, FlowableConstant.PV_BIZ_ID));
        setFlowCode(emptyIfNull(varMap, FlowableConstant.PV_BIZ_CODE));
        setFlowName(emptyIfNull(varMap, FlowableConstant.PV_BIZ_NAME));
        setInvokers(emptyIfNull(varMap, FlowableConstant.PV_HIS_INVOKERS));
        setResult(emptyIfNull(varMap, FlowableConstant.PV_AUDIT_RESULT));
        setState(emptyIfNull(varMap, FlowableConstant.PV_AUDIT_STATE));

        return this;
    }

    private String emptyIfNull(Map<String, HistoricVariableInstance> vars, String key) {
        HistoricVariableInstance h = vars.get(key);
        if (h == null) {
            return "";
        } else {
            if (h.getValue() == null) {
                return "";
            }
            return (String) h.getValue();
        }
    }
}
