package com.abt.wf.model;

import com.abt.common.entity.Company;
import com.abt.common.util.JsonUtil;
import com.abt.sys.model.entity.SystemFile;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.Reimburse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 *
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ReimburseForm extends Reimburse {

    private List<String> managerList;
    private List<SystemFile> attachments;

    //-- 当前正在进行的task
    private String currentTaskId;
    private String currentTaskDefId;
    private String currentTaskName;
    private String currentTaskAssigneeId;
    private String currentTaskAssigneeName;

    //-- 参与的task
    private String invokedTaskId;
    private String invokedTaskDefId;
    private String invokedTaskName;
    private String invokedTaskAssigneeId;
    private String invokedTaskAssigneeName;

    //-- 提交人
    private String submitUserid;
    private String submitUsername;

    //-- 审批
    private String decision;
    private String comment;

    //业务所属公司
    private Company bizCompany;


    private HashMap<String, Object> variableMap = new HashMap<>();

    //-- 流程参数key
    public static final String KEY_COST = "cost";
    public static final String KEY_IS_LEADER = "isLeader";
    public static final String KEY_MANAGER_LIST = "managerList";
    public static final String KEY_STARTER = "starter";

    public Map<String, Object> variableMap() {
        this.variableMap.clear();
        this.variableMap.put(KEY_COST, this.getCost());
        this.variableMap.put(KEY_IS_LEADER, this.isLeader());
        this.variableMap.put(KEY_MANAGER_LIST, this.getManagerList());
        this.variableMap.put(KEY_STARTER, this.getCreateUserid());
        return this.variableMap;
    }

    public boolean isPass() {
        return Constants.DECISION_PASS.equals(decision);
    }

    public boolean isReject() {
        return Constants.DECISION_REJECT.equals(decision);
    }


    public void prepareEntity() {
        this.setFileList(JsonUtil.convertJson(this.getAttachments()));
        if (!CollectionUtils.isEmpty(this.getManagerList())) {
            this.setManagers(String.join(",", this.getManagerList()));
        }
    }


    public Reimburse newEntityInstance() {
        Reimburse rbs = new Reimburse();
        rbs.setId(this.getId());
        rbs.setCost(this.getCost());
        rbs.setVoucherNum(this.getVoucherNum());
        rbs.setRbsDate(this.getRbsDate());
        rbs.setRbsType(this.getRbsType());
        rbs.setReason(this.getReason());
        rbs.setCompany(this.getCompany());
        rbs.setDepartmentId(this.getDepartmentId());
        rbs.setDepartmentName(this.getDepartmentName());
        rbs.setTeamId(this.getTeamId());
        rbs.setTeamName(this.getTeamName());
        rbs.setProject(this.getProject());
        rbs.setServiceName(this.getServiceName());
        rbs.setBusinessState(this.getBusinessState());
        rbs.setProcessState(this.getProcessState());
        rbs.setFinished(this.isFinished());
        rbs.setFileList(this.getFileList());
        rbs.setLeader(this.isLeader());
        rbs.setManagers(this.getManagers());
        rbs.setDeleteReason(this.getDeleteReason());
        rbs.setServiceName(this.getServiceName());
        rbs.setEndTime(this.getEndTime());
        rbs.setProcessInstanceId(this.getProcessInstanceId());
        rbs.setProcessDefinitionKey(this.getProcessDefinitionKey());
        rbs.setProcessDefinitionId(this.getProcessDefinitionId());
        return rbs;
    }

}
