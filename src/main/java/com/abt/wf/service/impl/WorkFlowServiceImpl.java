package com.abt.wf.service.impl;

import com.abt.sys.model.dto.UserView;
import com.abt.wf.model.ReimburseApplyForm;
import com.abt.wf.service.WorkFlowService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 流程服务
 */
@Service
@Slf4j
public class WorkFlowServiceImpl implements WorkFlowService {

    private final RuntimeService runtimeService;
    private final HistoryService historyService;

    public WorkFlowServiceImpl(RuntimeService runtimeService, HistoryService historyService) {
        this.runtimeService = runtimeService;
        this.historyService = historyService;
    }


    public void start(ReimburseApplyForm form, UserView user) {
        log.info("流程申请开始, 申请人-- id: {}, name: {}; 流程定义key: {}", user.getId(), user.getName(), form.getProcessDefId());

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(form.getProcessDefId());
    }

    /**
     * 准备数据
     */
    private Map<String, Object> prepareVars(ReimburseApplyForm form) {

    }

}
