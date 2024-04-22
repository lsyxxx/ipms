package com.abt.wf.service.impl;

import com.abt.common.model.User;
import com.abt.wf.config.WorkFlowConfig;
import com.abt.wf.model.TaskWrapper;
import com.abt.wf.service.ActivitiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
@Slf4j
public class ActivitiServiceImpl implements ActivitiService {

    private final WorkFlowConfig workFlowConfig;

    public ActivitiServiceImpl(WorkFlowConfig workFlowConfig) {
        this.workFlowConfig = workFlowConfig;
    }

    @Override
    public List<TaskWrapper> findFinanceTask(String assignee, String... defKeys) {
        return List.of();
    }

    @Override
    public List<User> findDefaultCopyUsers() {
        return workFlowConfig.workflowDefaultCopy();
    }
}
