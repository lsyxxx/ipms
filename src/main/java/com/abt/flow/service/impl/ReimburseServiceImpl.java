package com.abt.flow.service.impl;

import com.abt.flow.dao.BizFlowRelationMapper;
import com.abt.flow.service.ReimburseService;
import com.abt.sys.model.dto.UserView;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

/**
 * 报销流程
 */
@Service
public class ReimburseServiceImpl implements ReimburseService {

    private final BizFlowRelationMapper bizFlowRelationMapper;

    public ReimburseServiceImpl(BizFlowRelationMapper bizFlowRelationMapper) {
        this.bizFlowRelationMapper = bizFlowRelationMapper;
    }

    @Override
    public ProcessInstance start(String bizType, UserView user) {
        return null;
    }

    @Override
    public void completeTask(String taskId) {

    }

    @Override
    public void afterCompleted() {

    }
}
