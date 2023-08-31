package com.abt.flow.service.impl;

import com.abt.common.util.MessageUtil;
import com.abt.flow.model.Form;
import com.abt.flow.model.ProcessState;
import com.abt.flow.model.ProcessVo;
import com.abt.flow.model.ReimburseApplyForm;
import com.abt.flow.model.entity.BizFlowRelation;
import com.abt.flow.repository.BizFlowRelationRepository;
import com.abt.flow.service.FlowService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Slf4j
public class AbstractFlowService implements FlowService {

    private final BizFlowRelationRepository bizFlowRelationRepository;

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();


    /**
     * 流程中参数Key, value统一为Form对象
     * TODO: 因为流程中参数flowable会保存到数据库，考虑最小参数。
     */
    public static final String VAR_KEY = "form_";

    private final RuntimeService runtimeService;

    public AbstractFlowService(BizFlowRelationRepository bizFlowRelationRepository, RuntimeService runtimeService) {
        this.bizFlowRelationRepository = bizFlowRelationRepository;
        this.runtimeService = runtimeService;
    }

    @Override
    public ProcessInstance start(String bizType, UserView user, ReimburseApplyForm form) {
        setStartUser(user.getId());
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(form.getProcDefId(), form.getBusinessKey(), Map.of(VAR_KEY, form));
        //current task
        BizFlowRelation bizFlowRelation = initBizFlowRelation(form, processInstance.getProcessInstanceId(), user);
        bizFlowRelation = bizFlowRelationRepository.save(bizFlowRelation);
        ProcessVo process = new ProcessVo(bizFlowRelation, form);


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

    private BizFlowRelation initBizFlowRelation(Form form, String processInstanceId, UserView user) {
        BizFlowRelation bizFlowRelation = new BizFlowRelation();
        bizFlowRelation.setProcDefId(form.getProcDefId());
        bizFlowRelation.setBizCategoryId(form.getBizId());
        bizFlowRelation.setBizCategoryCode(form.getBizCode());
        bizFlowRelation.setBusinessKey(form.getBusinessKey());
        bizFlowRelation.setStartDate(LocalDate.now());
        bizFlowRelation.setStarterId(user.getId());
        bizFlowRelation.setStarterName(user.getName());
        bizFlowRelation.setProcInstId(processInstanceId);
        bizFlowRelation.setState(ProcessState.Active.code());

        return bizFlowRelation;
    }
}
