package com.abt.wfbak.model;

import com.abt.common.model.User;
import com.abt.common.util.JsonUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.SystemFile;
import com.abt.wfbak.entity.Reimburse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.task.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报销对象
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class ReimburseForm extends Reimburse {

    /**
     * 审批状态，记录列表中
     * 审批中/已结束/终止
     */
    private String approvalDesc;
    /**
     * 审批结果，记录列表中
     * 审批未通过/审批已通过/--(没有审批结果)
     */
    private String resultDesc;

    private List<SystemFile> attachments;
    private List<User> managers;

    public Map<String, Object> variableMap = new HashMap<>();
    /**
     * 提交的评论
     */
    private String comment;
    private String decision;

    /**
     * 提交表单的userid
     */
    private String submitUserid;
    private String submitUsername;

    /**
     *  当前Task节点(正在进行的)
     *  只有未结束的流程存在
     */
    private Task currentTask;

    //-- 当前节点用户
    private String currentTaskUserid;
    private String currentTaskUsername;


    public ReimburseForm() {
        super();
    }


    /*-----------------------
     * 审批状态。参考钉钉
     * ----------------------
     */
    public static final String STATE_APPROVING = "审批中";
    public static final String STATE_FINISHED = "已结束";
    public static final String STATE_TERMINATED = "终止";

    /*-----------------------
     * 审批详情(Task)中的状态。参考钉钉
     * ----------------------
     */
    public static final String STATE_REJECT = "已拒绝";
    public static final String STATE_PASS = "已通过";
    public static final String STATE_CANCEL = "已撤销";

    /*-----------------------
     * 审批结果。参考钉钉
     * ----------------------
     */
    //没有产生结果，包含审批中/撤销
    public static final String STATE_APPROVAL_NONE = "正在处理";
    public static final String STATE_APPROVAL_REJECT = "审批未通过";
    public static final String STATE_APPROVAL_PASS = "审批通过";
    /**
     * 暂存数据不在记录中查询得到。仅状态标识
     */
    public static final String STATE_TEMP = "暂存";

    public static final String REJECT = "reject";
    public static final String PASS = "pass";

    public static final String KEY_COST = "cost";
    public static final String KEY_IS_LEADER = "isLeader";
    public static final String KEY_MANAGER_LIST = "managerList";
    public static final String KEY_STARTER = "starter";

    public Map<String, Object> variableMap() {
        this.variableMap.clear();
        this.variableMap.put(KEY_COST, this.getCost());
        this.variableMap.put(KEY_IS_LEADER, this.isLeader());
        this.variableMap.put(KEY_MANAGER_LIST, this.getManagerList());
        this.variableMap.put(KEY_STARTER, this.getStarterId());
        return this.variableMap;
    }

    public boolean isReject() {
        return REJECT.equals(this.decision);
    }

    public boolean isPass() {
        return PASS.equals(this.decision);
    }


    /**
     * 审批状态描述
     */
    public String approvalDesc() {
        switch (this.getState()) {
            case Reimburse.STATE_APPROVING -> this.setApprovalDesc(STATE_APPROVING);
            case Reimburse.STATE_PASS, Reimburse.STATE_REJECT -> this.setApprovalDesc(STATE_FINISHED);
            case Reimburse.STATE_CANCEL -> this.setApprovalDesc(STATE_TERMINATED);
            case Reimburse.STATE_TEMP -> this.setApprovalDesc(STATE_TEMP);
            default -> log.warn("不存在的流程状态: " + this.getState());
        }
        return this.approvalDesc;
    }

    public String resultDesc() {
        switch (this.getState()) {
            case Reimburse.STATE_APPROVING, Reimburse.STATE_CANCEL, Reimburse.STATE_TEMP -> this.setResultDesc(STATE_APPROVAL_NONE);
            case Reimburse.STATE_PASS -> this.setResultDesc(STATE_APPROVAL_PASS);
            case Reimburse.STATE_REJECT -> this.setResultDesc(STATE_APPROVAL_REJECT);
        }
        return this.resultDesc;
    }

    private List<SystemFile> convertFileList() throws JsonProcessingException {
        return JsonUtil.ObjectMapper().readValue(this.getFileList(), new TypeReference<List<SystemFile>>() {});
    }

    public static ReimburseForm from(Reimburse entity) {
        ReimburseForm form = (ReimburseForm) entity;
        form.approvalDesc();
        form.resultDesc();
        try {
            form.convertFileList();
        } catch (Exception e) {
            log.error("附件Json数据转换失败", e);
            throw new BusinessException("附件Json数据转换失败 - " + e.getMessage());
        }
        return form;
    }


}
