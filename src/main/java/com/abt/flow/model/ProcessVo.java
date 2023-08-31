package com.abt.flow.model;

import com.abt.flow.model.entity.BizFlowRelation;
import com.abt.sys.model.dto.UserView;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class ProcessVo<T extends Form> implements Serializable {

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
    private UserView applicant;
    /**
     * 附件路径
     */
    private List<String> attachments = new ArrayList<>();

    /**
     * 所有审批信息
     */
    private List<Approval> approvals = new ArrayList<>();

    private ProcessState state;

    /**
     * 数据库信息
     */
    private BizFlowRelation relation;

    /**
     * 流程中的表单数据
     */
    private T form;


    public ProcessVo(BizFlowRelation relation, T form) {
        //根据数据库的读取创建processVo
//        this.user =
        this.relation = relation;
        this.state = ProcessState.of(relation.getState());
        this.applicant = new UserView().setId(relation.getStarterId()).setName(relation.getStarterName());
        this.form = form;
    }

    /**
     * 决策是否通过
     * @return
     */
    public boolean isApprove() {
        String str = String.valueOf(currentResult);
        return Decision.isApprove(str);
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
        int state = this.relation.getState();
        return ProcessState.Completed.equal(state) || ProcessState.Terminated.equal(state);
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
