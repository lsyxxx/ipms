package com.abt.flow.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 流程处理中的参数
 */
@Data
@Accessors(chain = true)
public class ProcessVo implements Serializable {

    /**
     * 当前处理用户
     */
    private String user;

    /**
     * 仅当前节点的结果
     * 一般是Decision
     */
    private Object currentResult;
    /**
     *  流程申请用户
     */
    private String applicant;
    /**
     * 附件路径
     */
    private List<String> attachments = new ArrayList<>();
    /**
     * 整个流程是否结束
     * 包括正常结束和异常结束
     */
    private boolean isFinished;

    /**
     * 所有审批信息
     */
    private List<Approval> approvals = new ArrayList<>();

    private ProcessState state;


    /**
     * 决策是否通过
     * @return
     */
    public boolean isApprove() {
        return StringUtils.equals(Decision.Approve.name(), String.valueOf(currentResult));
    }

    /**
     * 添加一条审批信息，只保存审批用户
     */
    public void addApprovalUser(String user, String decision) {
        this.approvals.add(new Approval(user, decision));
    }

    /**
     * 流程是否结束
     * 包括正常结束Completed和异常结束Terminated
     * @return
     */
    public boolean isFinished() {
        return this.isFinished = (this.state == ProcessState.Completed || this.state == ProcessState.Terminated);
    }


    /**
     * 是否正常进行
     * state = active
     * @return
     */
    public boolean isProcessing() {
        return this.state == ProcessState.Active;
    }
}
