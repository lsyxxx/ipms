package com.abt.wf.model;

import com.abt.wf.config.Constants;
import com.abt.wf.entity.Reimburse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;
import java.util.HashMap;

/**
 *
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ReimburseForm extends Reimburse {

    //-- 分页
    private int page;
    private int limit;

    //-- 当前正在进行的task
    private String currentTaskId;
    private String currentTaskName;
    private String currentTaskAssigneeId;
    private String currentTaskAssigneeName;
    /**
     *  详细状态
     * @see com.abt.wf.config.Constants STATE_DETAIL_
     */
    private String detailState;

    //-- 提交人
    private String submitUserid;
    private String submitUsername;

    //-- 审批
    private String decision;
    private String comment;


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

}
