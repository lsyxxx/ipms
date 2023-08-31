package com.abt.flow.service.impl;

import com.abt.flow.model.ProcessVo;
import com.abt.flow.model.ReimburseApplyForm;
import com.abt.flow.repository.BizFlowRelationRepository;
import com.abt.flow.service.ReimburseService;
import com.abt.sys.model.dto.UserView;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Service;

/**
 * 报销流程
 */
@Service
@Slf4j
public class ReimburseServiceImpl extends AbstractFlowService implements ReimburseService {

    private final RuntimeService runtimeService;
    private final BizFlowRelationRepository bizFlowRelationRepository;


    public ReimburseServiceImpl(BizFlowRelationRepository bizFlowRelationRepository, RuntimeService runtimeService) {
        super(bizFlowRelationRepository, runtimeService);
        this.runtimeService = runtimeService;
        this.bizFlowRelationRepository = bizFlowRelationRepository;
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
}
