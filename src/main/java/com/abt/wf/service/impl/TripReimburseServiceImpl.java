package com.abt.wf.service.impl;

import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.model.ReimburseForm;
import com.abt.wf.model.TripReimburseForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.TripReimburseService;
import org.camunda.bpm.engine.IdentityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 */

@Service
public class TripReimburseServiceImpl extends AbstractWorkflowCommonServiceImpl<TripReimburseForm> implements TripReimburseService {
    private final FlowOperationLogService flowOperationLogService;
    private final IdentityService identityService;

    public TripReimburseServiceImpl(FlowOperationLogService flowOperationLogService, IdentityService identityService) {
        super(identityService, flowOperationLogService);
        this.flowOperationLogService = flowOperationLogService;
        this.identityService = identityService;
    }

    @Override
    public Map<String, Object> createVariableMap(TripReimburseForm form) {
        return null;
    }

    @Override
    public String businessKey(TripReimburseForm form) {
        return Constants.SERVICE_TRIP;
    }

    @Override
    public Map<String, Object> getVariableMap() {
        return null;
    }

    @Override
    public void afterTask(TripReimburseForm form) {

    }

    @Override
    public void apply(TripReimburseForm form) {
        //prepare

        //start instance

        //save entity

        //record
    }

    @Override
    public void approve(TripReimburseForm form) {

    }

    @Override
    public void setAuthUser(String userid) {
        identityService.setAuthenticatedUserId(userid);
    }

    @Override
    public void clearAuthUser() {

    }

    @Override
    public void revoke(String entityId) {

    }

    @Override
    public void delete(String entityId) {

    }

    @Override
    public List<UserTaskDTO> preview(TripReimburseForm form) {
        return null;
    }

    @Override
    public List<FlowOperationLog> processRecord(String entityId) {
        return null;
    }

    @Override
    public void ensureEntityId(TripReimburseForm form) {

    }

    @Override
    public boolean isApproveUser(ReimburseForm form) {
        return false;
    }

    @Override
    public String notifyLink(String id) {
        return null;
    }
}
