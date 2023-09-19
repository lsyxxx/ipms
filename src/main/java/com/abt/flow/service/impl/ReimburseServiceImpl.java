package com.abt.flow.service.impl;

import com.abt.common.model.User;
import com.abt.common.validator.ValidationResult;
import com.abt.common.validator.ValidatorChain;
import com.abt.flow.config.FlowableConstant;
import com.abt.flow.model.*;
import com.abt.flow.model.entity.Reimburse;
import com.abt.flow.repository.FlowCategoryRepository;
import com.abt.flow.repository.ReimburseRepository;
import com.abt.flow.service.FlowOperationLogService;
import com.abt.flow.service.ReimburseService;
import com.abt.sys.exception.BadRequestParameterException;
import com.abt.sys.model.dto.UserView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.abt.flow.model.ProcessState.Active;

/**
 * 报销流程
 */
@Service
@Slf4j
public class ReimburseServiceImpl extends AbstractDefaultFlowService implements ReimburseService {

    private final HistoryService historyService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final FlowableConstant flowableConstant;

    private final ReimburseRepository reimburseRepository;


    //报销事由,报销金额,票据数量,报销日期
    private final ValidatorChain applyFormValidatorChain;

    private final ValidatorChain commonDecisionValidatorChain;

    private final Map<String, User> defaultAuditor;



    public ReimburseServiceImpl(RuntimeService runtimeService, TaskService taskService, HistoryService historyService, RepositoryService repositoryService, FlowOperationLogService flowOperationLogService, FlowableConstant flowableConstant, ReimburseRepository reimburseRepository, FlowCategoryRepository flowCategoryRepository, ValidatorChain applyFormValidatorChain, ValidatorChain commonDecisionValidatorChain,@Qualifier("flowDefaultAuditorMap") Map<String, User> defaultAuditor) {
        super(runtimeService, taskService, historyService, repositoryService, flowableConstant, flowOperationLogService);
        this.runtimeService = runtimeService;
        this.historyService = historyService;
        this.taskService = taskService;
        this.flowableConstant = flowableConstant;
        this.reimburseRepository = reimburseRepository;
        this.applyFormValidatorChain = applyFormValidatorChain;
        this.commonDecisionValidatorChain = commonDecisionValidatorChain;

        this.defaultAuditor = defaultAuditor;
    }

    /**
     * 0. 创建业务对象
     * 1. 启动流程
     * 2. 完成申请task
     * 3. 更新业务对象
     * @param user 申请用户
     * @param applyForm 申请表单
     */
    @Override
    public void apply(UserView user, ReimburseApplyForm applyForm) {
        log.info("开始执行[报销流程] - [申请], 申请用户: {}, 流程类型: {}", user.simpleInfo(), applyForm.getFlowType().getName());
        validateApplyForm(applyForm);

        Reimburse rbs = new Reimburse();
        rbs.create(applyForm, user);

        //添加流程参数
        Map<String, Object> processVars = initProcessVars(applyForm);
        processVars.put(FlowableConstant.PV_APPLICANT, new User(user));

        ProcessInstance processInstance = start(user, applyForm.getFlowType().getProcDefId(), rbs.getId(), processVars);
        String procId = processInstance.getId();

        //verify
        verifyRunningProcess(procId, messages.getMessage("flow.service.ReimburseServiceImpl.apply.start.error"));

        Task activeTask = getActiveTask(procId, messages.getMessage("flow.service.ReimburseServiceImpl.apply.start.error1"));

        completeTask(activeTask.getId());

        rbs.update(procId, activeTask, user.getId(), user.getName(), Active.value(), null);

        runtimeService.updateBusinessStatus(processInstance.getId(), businessKey(null, Active.value()));
        reimburseRepository.save(rbs);

    }
    /**
     * 一般审批包含角色
     * 包括：部门审批人(deptManager)，技术负责人(techManager)，总经理(ceo)，财务总监(fiManager)，税务(texOfficer)，会计(accountancy)
     * 下一个审批人(nextAssignee，这个流程没有)
     */
    private Map<String, Object> initProcessVars(ReimburseApplyForm form) {
        Map<String, Object> processVars = new HashMap<>();
        processVars.put(FlowableConstant.PV_BIZ_NAME, form.getFlowType().getName());
        processVars.put(FlowableConstant.PV_BIZ_ID, form.getFlowType().getId());
        processVars.put(FlowableConstant.PV_BIZ_CODE, form.getFlowType().getCode());

        processVars.put(FlowableConstant.PV_DEPT_MANAGER, form.getDeptManager());
        processVars.put(FlowableConstant.PV_TECH_MANAGER, form.getTechManager());
        processVars.put(FlowableConstant.PV_HIS_INVOKERS, "");

        processVars.putAll(defaultAuditor);

        return processVars;
    }

    private void validateApplyForm(ReimburseApplyForm applyForm) {
        ValidationResult result = applyFormValidatorChain.validate(applyForm.getDescription(), applyForm.getCost(), applyForm.getVoucherNum(), applyForm.getRbsDate());
        if (!result.isValid()) {
            log.error("申请表单参数验证失败！错误信息 - {}", result.getErrorMessage());
            throw new BadRequestParameterException(result.getErrorMessage());
        }
    }

    @Override
    public void departmentAudit(UserView user, ReimburseApplyForm applyForm) {
        String procId = applyForm.getProcessInstanceId();
        log.info("开始执行[报销流程] - [部门审核]: 审批人: {}, 流程id: {}", user.simpleInfo(), procId);
        decisionValidate(applyForm.getDecision());

        runtimeService.setVariable(procId, FlowableConstant.PV_DEPT_MANAGER, new User(user));
        check(user, applyForm);
    }

    private void decisionValidate(String decision) {
        ValidationResult result = commonDecisionValidatorChain.validateOne(decision);
        if (!result.isValid()) {
            log.error("决策参数验证失败！错误信息 - {}", result.getErrorMessage());
            throw new BadRequestParameterException(result.getErrorMessage());
        }
    }

    @Override
    public void techLeadAudit(UserView user, ReimburseApplyForm applyForm) {
        String procId = applyForm.getProcessInstanceId();
        log.info("开始执行[报销流程] - [技术负责人审核]: 审批人: {}, 流程id: {}", user.simpleInfo(), procId);

        decisionValidate(applyForm.getDecision());

        runtimeService.setVariable(procId, FlowableConstant.PV_TECH_MANAGER, new User(user));
        check(user, applyForm);

    }

    private Reimburse check(UserView user, ReimburseApplyForm applyForm) {
        String procId = applyForm.getProcessInstanceId();

        //1. verify
        verifyRunningProcess(procId);

        Task activeTask = getActiveTask(procId, null);
        String taskId = activeTask.getId();
        Decision decision = Decision.fromValue(applyForm.getDecision());
        ProcessState state = ProcessState.of(decision);
        if (StringUtils.hasLength(applyForm.getComment())) {
            taskService.addComment(taskId, procId, applyForm.getComment());
        }

        //2. check
        addInvokers(procId, user.getId());
        runtimeService.updateBusinessStatus(procId, businessKey(decision.value(), state.value()));
        this.check(user, decision, procId, taskId);

        //3. update
        Reimburse rbs = reimburseRepository.findByProcessInstanceId(procId);
        rbs.update(procId, activeTask, user.getId(), user.getName(), state.value(), decision.description());
        reimburseRepository.save(rbs);

        return rbs;
    }

    @Override
    public Reimburse ceoAudit(UserView user, ReimburseApplyForm applyForm) {
        String procId = applyForm.getProcessInstanceId();
        log.info("开始执行[报销流程] - [总经理审核]: 审批人: {}, 流程id: {}", user.simpleInfo(), procId);

        decisionValidate(applyForm.getDecision());

        runtimeService.setVariable(procId, FlowableConstant.PV_CEO, new User(user));
        Reimburse rbs = check(user, applyForm);

        return rbs;
    }

    @Override
    public Reimburse accountantAudit(UserView user, ReimburseApplyForm applyForm) {
        String procId = applyForm.getProcessInstanceId();
        log.info("开始执行[报销流程] - [财务会计审批]: 审批人: {}, 流程id: {}", user.simpleInfo(), procId);

        decisionValidate(applyForm.getDecision());

        runtimeService.setVariable(procId, FlowableConstant.PV_ACCOUNTANCY, new User(user));
        Reimburse rbs = check(user, applyForm);

        return rbs;
    }

    @Override
    public Reimburse financeManagerAudit(UserView user, ReimburseApplyForm applyForm) {
        String procId = applyForm.getProcessInstanceId();
        log.info("开始执行[报销流程] - [财务主管审批]: 审批人: {}, 流程id: {}", user.simpleInfo(), procId);

        decisionValidate(applyForm.getDecision());

        runtimeService.setVariable(procId, FlowableConstant.PV_FI_MANAGER, new User(user));
        Reimburse rbs = check(user, applyForm);

        return rbs;
    }

    @Override
    public Reimburse taxOfficerAudit(UserView user, ReimburseApplyForm applyForm) {
        String procId = applyForm.getProcessInstanceId();
        log.info("开始执行[报销流程] - [财务主管审批]: 审批人: {}, 流程id: {}", user.simpleInfo(), procId);

        decisionValidate(applyForm.getDecision());

        runtimeService.setVariable(procId, FlowableConstant.PV_TAX_OFFICER, new User(user));
        Reimburse rbs = check(user, applyForm);

        return rbs;
    }


    private void addInvokers(String procId, String user) {
        Object obj = runtimeService.getVariable(procId, FlowableConstant.PV_HIS_INVOKERS);
        String invokers = emptyIfNull(obj);
        if (StringUtils.hasLength(invokers)) {
            invokers  = invokers + ", " + user;
        }
        runtimeService.setVariable(procId, FlowableConstant.PV_HIS_INVOKERS, invokers);
    }

}
