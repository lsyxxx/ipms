package com.abt.flow.service.impl;

import com.abt.common.validator.ValidationResult;
import com.abt.common.validator.ValidatorChain;
import com.abt.flow.config.FlowableConstant;
import com.abt.flow.model.Decision;
import com.abt.flow.model.ProcessVo;
import com.abt.flow.model.ReimburseApplyForm;
import com.abt.flow.repository.BizFlowRelationRepository;
import com.abt.flow.service.FlowOperationLogService;
import com.abt.flow.service.FormBaseService;
import com.abt.flow.service.ReimburseService;
import com.abt.sys.exception.BadRequestParameterException;
import com.abt.sys.model.dto.UserView;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 报销流程
 */
@Service
@Slf4j
public class ReimburseServiceImpl extends AbstractFlowService implements ReimburseService {

    private final HistoryService historyService;
    private final RuntimeService runtimeService;
    private final BizFlowRelationRepository bizFlowRelationRepository;

    private final RepositoryService repositoryService;

    private final FlowOperationLogService flowOperationLogService;

    private final FormBaseService formBaseService;

    private final FlowableConstant flowableConstant;

    //报销事由,报销金额,票据数量,报销日期
    private final ValidatorChain applyFormValidatorChain;

    private final ValidatorChain commonDecisionValidatorChain;


    public ReimburseServiceImpl(BizFlowRelationRepository bizFlowRelationRepository, RuntimeService runtimeService, TaskService taskService, HistoryService historyService, RepositoryService repositoryService, FlowOperationLogService flowOperationLogService, FormBaseService formBaseService, FlowableConstant flowableConstant, ValidatorChain applyFormValidatorChain, ValidatorChain commonDecisionValidatorChain) {
        super(bizFlowRelationRepository, runtimeService, taskService, historyService, repositoryService, flowOperationLogService, flowableConstant);
        this.runtimeService = runtimeService;
        this.bizFlowRelationRepository = bizFlowRelationRepository;
        this.historyService = historyService;
        this.repositoryService = repositoryService;
        this.flowOperationLogService = flowOperationLogService;
        this.formBaseService = formBaseService;
        this.flowableConstant = flowableConstant;
        this.applyFormValidatorChain = applyFormValidatorChain;
        this.commonDecisionValidatorChain = commonDecisionValidatorChain;

    }

    @Override
    public ProcessVo<ReimburseApplyForm> apply(UserView user, ReimburseApplyForm applyForm) {
        log.info("开始执行[报销流程] - [申请]");
        ProcessVo<ReimburseApplyForm> vo = createProcessVo(user, applyForm);
        ValidationResult result = applyFormValidatorChain.validate(applyForm.getDescription(), applyForm.getCost(), applyForm.getVoucherNum(), applyForm.getDateTime());
        if (!result.isValid()) {
            log.error("申请表单参数验证失败！错误信息 - {}", result.getErrorMessage());
            throw new BadRequestParameterException(result.getErrorMessage());
        }
        apply(applyForm.getBizCode(), user, vo);
        return vo;
    }


    /**
     * 创建process vo
     * @param user 当前用户
     * @param applyForm 表单
     */
    private ProcessVo<ReimburseApplyForm> createProcessVo(UserView user, ReimburseApplyForm applyForm) {
        ProcessVo<ReimburseApplyForm> vo = new ProcessVo(user, applyForm);
        vo.setComment(applyForm.getComment());
        vo.setCurrentResult(Decision.fromValue(applyForm.getDecision()));
        return vo;
    }


    @Override
    public ProcessVo<ReimburseApplyForm> departmentAudit(UserView user, ReimburseApplyForm applyForm) {
        log.info("开始执行[报销流程] - [部门审核]");
        ProcessVo<ReimburseApplyForm> vo = createProcessVo(user, applyForm);
        decisionValidate(applyForm.getDecision());
        this.check(user, vo);
        return vo;
    }

    private void decisionValidate(String decision) {
        ValidationResult result = commonDecisionValidatorChain.validateOne(decision);
        if (!result.isValid()) {
            log.error("决策参数验证失败！错误信息 - {}", result.getErrorMessage());
            throw new BadRequestParameterException(result.getErrorMessage());
        }
    }

    @Override
    public ProcessVo<ReimburseApplyForm> techLeadAudit(UserView user, ReimburseApplyForm applyForm) {
        log.info("开始执行[报销流程] - [技术负责人审核]");
        ProcessVo<ReimburseApplyForm> vo = createProcessVo(user, applyForm);
        decisionValidate(applyForm.getDecision());
        this.check(user, vo);
        return vo;
    }

    @Override
    public ProcessVo<ReimburseApplyForm> ceoAudit(UserView userVo, ReimburseApplyForm applyForm) {
        return null;
    }

    @Override
    public ProcessVo<ReimburseApplyForm> accountancyAudit(UserView userVo, ReimburseApplyForm applyForm) {
        return null;
    }


    @Override
    void beforeComplete(UserView user, ProcessVo vo) {
        //TODO: 添加评论
    }

    @Override
    String customName(String... strings) {
        return null;
    }

    @Override
    Map<String, Object> initProcessVariables(ProcessVo processVo) {
        return new HashMap<>();
    }

    @Override
    Map<String, Object> requiredExecutionVariables(ProcessVo processVo) {
        return null;
    }


    @Override
    public void rejectTask(UserView user, ProcessVo vo) {

    }



}
