package com.abt.wf.service.impl;

import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.WorkflowBase;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.WorkFlowService;
import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.IdentityService;

import java.util.List;

/**
 *
 */
@AllArgsConstructor
public abstract class AbstractWorkflowCommonServiceImpl<T> implements WorkFlowService<T> {

    private IdentityService identityService;
    private FlowOperationLogService flowOperationLogService;

    @Override
    public void setAuthUser(String userid) {
        identityService.setAuthenticatedUserId(userid);
    }

    @Override
    public List<FlowOperationLog> getCompletedOperationLogByEntityId(String entityId) {
        return flowOperationLogService.findLogsByEntityId(entityId);
    }

    @Override
    public void clearAuthUser() {
        identityService.clearAuthentication();
    }


}
