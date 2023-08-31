package com.abt.flow.service.impl;

import com.abt.flow.model.Form;
import com.abt.flow.model.ProcessVo;
import com.abt.flow.model.ReimburseApplyForm;
import com.abt.flow.model.entity.BizFlowRelation;
import com.abt.flow.repository.BizFlowRelationRepository;
import com.abt.flow.service.FlowService;
import com.abt.sys.model.dto.UserView;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Slf4j
public class AbstractFlowServiceImpl implements FlowService {

    private final BizFlowRelationRepository bizFlowRelationRepository;


    /**
     * 流程中参数Key, value统一为Form对象
     * TODO: 因为流程中参数flowable会保存到数据库，考虑最小参数。
     */
    public static final String VAR_KEY = "form_";

    private final RuntimeService runtimeService;

    public AbstractFlowServiceImpl(BizFlowRelationRepository bizFlowRelationRepository, RuntimeService runtimeService) {
        this.bizFlowRelationRepository = bizFlowRelationRepository;
        this.runtimeService = runtimeService;
    }

    @Override
    public ProcessInstance start(String bizType, UserView user, ReimburseApplyForm form) {
        //
        setStartUser(user.getId());

        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceById(form.getProcDefId(), form.getBusinessKey(), Map.of(VAR_KEY, form));
            ProcessVo process = new ProcessVo();
            BizFlowRelation bizFlowRelation = new BizFlowRelation();
        } catch(Exception e) {
            log.error("启动流程失败！ -- {}", e.getMessage());
            log.error(e.getLocalizedMessage());
        }


        clearAuthenticationId();
        return null;
    }

    @Override
    public void completeTask(String taskId) {

    }

    @Override
    public void afterCompleted() {

    }

    @Override
    public List getOperationLogs(String processInstanceId) {
        return null;
    }

    @Override
    public InputStream getPngDiagramWithHighLightedTask(String processInstanceI) {
        return null;
    }

    /**
     * 设置启动用户
     * @param user
     */
    protected void setStartUser(String user) {
        Authentication.setAuthenticatedUserId(user);
    }

    protected void clearAuthenticationId() {
        Authentication.setAuthenticatedUserId(null);
    }

    private BizFlowRelation initBizFlowRelation(Form form, String processInstanceId) {
        BizFlowRelation bizFlowRelation = new BizFlowRelation();
        bizFlowRelation.setProcDefId(form.getProcDefId());

        return bizFlowRelation;
    }
}
