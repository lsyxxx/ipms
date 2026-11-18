package com.abt.wf.model;

import com.abt.common.model.User;
import com.abt.wf.entity.act.ActHiTaskInst;
import com.abt.wf.entity.act.ActRuTask;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.camunda.bpm.engine.task.DelegationState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 合同开票记录，保留原开票记录返回字段并补充合同含税信息。
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractInvoiceVO {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;
    private String updateUserid;
    private String updateUsername;
    private String createUserid;
    private String createUsername;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;
    private String processDefinitionKey;
    private String processDefinitionId;
    private String processInstanceId;
    private String businessState;
    private String processState;
    private boolean isFinished;
    private String serviceName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;
    private String deleteReason;
    private boolean isDelete;
    private String createDeptId;
    private String createDeptName;
    private String createTeamId;
    private String createTeamName;
    private String copy;
    private ActRuTask currentTask;
    private List<ActHiTaskInst> historicTaskList;
    private String currentTaskId;
    private String currentTaskDefId;
    private String currentTaskName;
    private String currentTaskAssigneeId;
    private String currentTaskAssigneeName;
    private LocalDateTime currentTaskStartTime;
    private List<User> candidateUsers;
    private boolean isApproveUser;
    private String invokedTaskId;
    private String invokedTaskName;
    private String invokedTaskAssigneeId;
    private String invokedTaskAssigneeName;
    private String invokedTaskDefId;
    private String submitUserid;
    private String submitUsername;
    private String checkItemJson;
    private String delegateUser;
    private DelegationState delegateState;
    private List<String> briefDesc;
    private String id;
    private String invoiceType;
    private String project;
    private double invoiceAmount;
    private String clientId;
    private String clientName;
    private String address;
    private String taxNo;
    private String telephoneNo;
    private String bank;
    private String account;
    private Double contractAmount;
    private String contractNo;
    private String contractName;
    private String remark;
    private String managers;
    private String deptId;
    private String deptName;
    private String teamId;
    private String teamName;
    private String company;
    private String fileList;
    private String invoiceFile;
    private String invoiceRemark;
    private String testNo;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime issueDate;
    private String issuerId;
    private String issuerName;
    private String settlementId;
    private Boolean isSocial;
    private String comment;
    private String decision;
    private Map<String, Object> variableMap;

    /**
     * 合同是否含税：true 含税，false 不含税，null 未找到对应合同。
     */
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Boolean includeTax;

    /**
     * 当前任务是否被转办，沿用原返回字段。
     */
    public boolean isCurrentTaskDelegated() {
        return DelegationState.PENDING.equals(delegateState);
    }
}
