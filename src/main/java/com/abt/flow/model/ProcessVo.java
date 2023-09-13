package com.abt.flow.model;

import com.abt.common.model.User;
import com.abt.flow.model.entity.BizFlowRelation;
import com.abt.sys.model.dto.UserView;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 流程处理中的用到的数据
 * 字段涵盖所有流程相关的参数
 * 用于在流程处理中传递数据
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ProcessVo<T extends FlowForm> implements Serializable {

    /**
     * 仅当前节点的审批结果
     * 非审批节点结果=null
     */
    private Decision currentResult = null;
    /**
     *  流程申请/启动用户
     */
    private User<String> starter;

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

    private String processInstanceId;

    /**
     * 最近完成的task id
     */
    private String taskId;
    private String taskName;

    /**
     * 流程参数
     * 无法remove(key)
     */
    private Map<String, Object> processVariables = Map.of();

    /**
     * 下一个处理用户
     */
    private String nextAssignee;

    /**
     * 当前审批评论
     */
    private String comment;


    /**
     * 创建
     * @param user 当前用户
     * @param form 当前表单
     */
    public ProcessVo(UserView user, T form) {
//        this.user = user.getId();
        this.form = form;
        this.processInstanceId = form.getProcessInstanceId();
    }

    public ProcessVo(BizFlowRelation relation, T form) {
        //根据数据库的读取创建processVo
//        this.user =
        this.relation = relation;
        this.state = ProcessState.of(relation.getState());
//        this.applicant = new UserView().setId(relation.getStarterId()).setName(relation.getStarterName());
        this.processInstanceId = relation.getProcInstId();
        this.form = form;
    }

    public ProcessVo copyOf(BizFlowRelation relation, T form) {
        this.relation = relation;
        this.state = ProcessState.of(relation.getState());
//        this.applicant = new UserView().setId(relation.getStarterId()).setName(relation.getStarterName());
        this.processInstanceId = relation.getProcInstId();
        this.form = form;
        return this;
    }

    /**
     * 决策是否通过
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
     */
    public boolean isFinished() {
        int state = this.relation.getState();
        return ProcessState.Completed.equal(state) || ProcessState.Terminated.equal(state);
    }


    /**
     * 是否正常进行
     * state = active
     */
    public boolean isProcessing() {
        return this.state == ProcessState.Active;
    }


    public T get() {
        return this.form;
    }


    /**
     * 更新task相关信息
     * @param task flowable Task
     */
    public ProcessVo updateBy(Task task) {
        this.setTaskName(task.getName());
        this.setTaskId(task.getId());
//        form.updateProcess(this.processInstanceId, task.getId());

        return this;
    }

    /**
     * 添加评论
     * @return
     */
    public ProcessVo addApproval() {
        approvals = ListUtils.emptyIfNull(this.approvals);
//        approvals.add(new Approval(this.user, this.currentResult, this.comment));
        return this;
    }
}
