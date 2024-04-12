package com.abt.wf.service.impl;

import com.abt.common.model.RequestForm;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.service.UserService;
import com.abt.wf.model.ReimburseForm;
import com.abt.wf.model.ReimburseRequestForm;
import com.abt.wf.model.TaskWrapper;
import com.abt.wf.service.ActivitiService;
import com.abt.wf.service.InvoiceApplyService;
import com.abt.wf.service.ReimburseService;
import com.abt.wf.service.TripReimburseService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
@Slf4j
public class ActivitiServiceImpl implements ActivitiService {

    private final TaskService taskService;
    private final RuntimeService runtimeService;
    private final HistoryService historyService;
    private final UserService userService;
    private final ReimburseService reimburseService;
    private final TripReimburseService tripReimburseService;
    private final InvoiceApplyService invoiceApplyService;

    public ActivitiServiceImpl(TaskService taskService, RuntimeService runtimeService, HistoryService historyService,
                               @Qualifier("sqlServerUserService") UserService userService, ReimburseService reimburseService, TripReimburseService tripReimburseService, InvoiceApplyService invoiceApplyService) {
        this.taskService = taskService;
        this.runtimeService = runtimeService;
        this.historyService = historyService;
        this.userService = userService;
        this.reimburseService = reimburseService;
        this.tripReimburseService = tripReimburseService;
        this.invoiceApplyService = invoiceApplyService;
    }

    @Override
    public List<TaskWrapper> findFinanceTask(String assignee, String... defKeys) {
        return List.of();
    }
}
