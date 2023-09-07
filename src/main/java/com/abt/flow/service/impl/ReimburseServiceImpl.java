package com.abt.flow.service.impl;

import com.abt.flow.config.FlowableConfig;
import com.abt.flow.config.FlowableConstant;
import com.abt.flow.model.ProcessVo;
import com.abt.flow.model.ReimburseApplyForm;
import com.abt.flow.repository.BizFlowRelationRepository;
import com.abt.flow.service.FlowOperationLogService;
import com.abt.flow.service.ReimburseService;
import com.abt.sys.model.dto.UserView;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    private final FlowableConstant flowableConstant;


    public ReimburseServiceImpl(BizFlowRelationRepository bizFlowRelationRepository, RuntimeService runtimeService, TaskService taskService, HistoryService historyService, RepositoryService repositoryService, FlowOperationLogService flowOperationLogService, FlowableConstant flowableConstant) {
        super(bizFlowRelationRepository, runtimeService, taskService, historyService, repositoryService, flowOperationLogService, flowableConstant);
        this.runtimeService = runtimeService;
        this.bizFlowRelationRepository = bizFlowRelationRepository;
        this.historyService = historyService;
        this.repositoryService = repositoryService;
        this.flowOperationLogService = flowOperationLogService;
        this.flowableConstant = flowableConstant;
    }

    @Override
    public ProcessVo apply(UserView user, ReimburseApplyForm applyForm) {
        return null;
    }

    @Override
    public ProcessVo departmentAudit(ProcessVo process, ReimburseApplyForm applyForm) {
        return null;
    }

    @Override
    public ProcessVo techLeadAudit(ProcessVo process, ReimburseApplyForm applyForm) {
        return null;
    }

    @Override
    public ProcessVo ceoAudit(ProcessVo processVo, ReimburseApplyForm applyForm) {
        return null;
    }

    @Override
    public ProcessVo accountancyAudit(ProcessVo processVo, ReimburseApplyForm applyForm) {
        return null;
    }

    @Override
    void beforeComplete(UserView user, ProcessVo vo) {

    }

    @Override
    String customName(String... strings) {
        return null;
    }

    @Override
    Map<String, Object> initProcessVariables(Map processVariables) {
        return null;
    }

    @Override
    Map<String, Object> addProcessVariables(Map processVariables, Map.Entry[] entries) {
        return null;
    }

}
