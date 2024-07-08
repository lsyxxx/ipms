package com.abt.wf.service.impl;

import com.abt.common.model.User;
import com.abt.wf.config.Constants;
import com.abt.wf.config.WorkFlowConfig;
import com.abt.wf.entity.WorkflowBase;
import com.abt.wf.model.*;
import com.abt.wf.service.*;
import com.abt.wf.util.WorkFlowUtil;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
@Service
@Slf4j
public class ActivitiServiceImpl implements ActivitiService {

    private final WorkFlowConfig workFlowConfig;
    private final TaskService taskService;
    private final HistoryService historyService;
    private final RuntimeService runtimeService;

    private final Map<String, BusinessService> serviceMap;

    private final ReimburseService reimburseService;
    private final TripService tripService;
    private final InvoiceApplyService invoiceApplyService;
    private final InvoiceOffsetService invoiceOffsetService;
    private final LoanService loanService;
    private final PayVoucherService payVoucherService;

    public ActivitiServiceImpl(WorkFlowConfig workFlowConfig, TaskService taskService, HistoryService historyService, RuntimeService runtimeService, Map<String, BusinessService> serviceMap, ReimburseService reimburseService, TripService tripService, InvoiceApplyService invoiceApplyService, InvoiceOffsetService invoiceOffsetService, LoanService loanService, PayVoucherService payVoucherService) {
        this.workFlowConfig = workFlowConfig;
        this.taskService = taskService;
        this.historyService = historyService;
        this.runtimeService = runtimeService;
        this.serviceMap = serviceMap;
        this.reimburseService = reimburseService;
        this.tripService = tripService;
        this.invoiceApplyService = invoiceApplyService;
        this.invoiceOffsetService = invoiceOffsetService;
        this.loanService = loanService;
        this.payVoucherService = payVoucherService;
    }
    @Override
    public List<WorkflowBase> findUserTodoAll(String userid, String query, int page, int limit) {
        List<WorkflowBase> list = new ArrayList<>();

        TripRequestForm tripForm = new TripRequestForm();
        tripForm.setPage(1);
        tripForm.setLimit(9999);
        tripForm.setUserid(userid);
        tripForm.setQuery(query);
        list.addAll(tripService.findMyTodoByQueryPageable(tripForm).getContent());

        InvoiceApplyRequestForm invForm = new InvoiceApplyRequestForm();
        invForm.setPage(1);
        invForm.setLimit(9999);
        invForm.setUserid(userid);
        invForm.setQuery(query);
        list.addAll(invoiceApplyService.findMyTodoByQueryPageable(invForm).getContent());


        PayVoucherRequestForm payForm = new PayVoucherRequestForm();
        payForm.setPage(1);
        payForm.setLimit(9999);
        payForm.setUserid(userid);
        payForm.setQuery(query);
        list.addAll(payVoucherService.findMyTodoByQueryPageable(payForm).getContent());


        LoanRequestForm loanForm = new LoanRequestForm();
        loanForm.setPage(1);
        loanForm.setLimit(9999);
        loanForm.setUserid(userid);
        loanForm.setQuery(query);
        list.addAll(loanService.findMyTodoByQueryPageable(loanForm).getContent());


        ReimburseRequestForm rbsForm = new ReimburseRequestForm();
        rbsForm.setPage(1);
        rbsForm.setLimit(9999);
        rbsForm.setUserid(userid);
        rbsForm.setQuery(query);
        list.addAll(reimburseService.findMyTodoByQueryPageable(rbsForm).getContent());

        InvoiceOffsetRequestForm ioForm = new InvoiceOffsetRequestForm();
        ioForm.setPage(1);
        ioForm.setLimit(9999);
        ioForm.setUserid(userid);
        ioForm.setQuery(query);
        list.addAll(invoiceOffsetService.findMyTodoByQueryPageable(ioForm).getContent());

        list.sort(this::compareWorkflowBase);
        return list;
    }

    @Override
    public List<WorkflowBase>  findDoneByQuery(String userid, String query, int page, int limit) {
        List<WorkflowBase> list = new ArrayList<>();

        TripRequestForm tripForm = new TripRequestForm();
        tripForm.setPage(1);
        tripForm.setLimit(9999);
        tripForm.setUserid(userid);
        tripForm.setQuery(query);
        list.addAll(tripService.findMyDoneByQueryPageable(tripForm).getContent());

        InvoiceApplyRequestForm invForm = new InvoiceApplyRequestForm();
        invForm.setPage(1);
        invForm.setLimit(9999);
        invForm.setUserid(userid);
        invForm.setQuery(query);
        list.addAll(invoiceApplyService.findMyDoneByQueryPageable(invForm).getContent());


        PayVoucherRequestForm payForm = new PayVoucherRequestForm();
        payForm.setPage(1);
        payForm.setLimit(9999);
        payForm.setUserid(userid);
        payForm.setQuery(query);
        list.addAll(payVoucherService.findMyDoneByQueryPageable(payForm).getContent());


        LoanRequestForm loanForm = new LoanRequestForm();
        loanForm.setPage(1);
        loanForm.setLimit(9999);
        loanForm.setUserid(userid);
        loanForm.setQuery(query);
        list.addAll(loanService.findMyDoneByQueryPageable(loanForm).getContent());


        ReimburseRequestForm rbsForm = new ReimburseRequestForm();
        rbsForm.setPage(1);
        rbsForm.setLimit(9999);
        rbsForm.setUserid(userid);
        rbsForm.setQuery(query);
        list.addAll(reimburseService.findMyDoneByQueryPageable(rbsForm).getContent());

        InvoiceOffsetRequestForm ioForm = new InvoiceOffsetRequestForm();
        ioForm.setPage(1);
        ioForm.setLimit(9999);
        ioForm.setUserid(userid);
        ioForm.setQuery(query);
        list.addAll(invoiceOffsetService.findMyDoneByQueryPageable(ioForm).getContent());

        list.sort(this::compareWorkflowBase);
        return list;
    }
    private int compareWorkflowBase(WorkflowBase o1, WorkflowBase o2) {
        if (o1.getCreateDate().isBefore(o2.getCreateDate())) {
            return 1;
        } else if (o1.getCreateDate().isAfter(o2.getCreateDate())) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public WorkflowBase findFinanceTask(String assignee) {
        return findUserTodoLatest1ByProcessDefinitionKeys(assignee, WorkFlowConfig.financeWorkflowDef);
    }

    @Override
    public List<User> findDefaultCopyUsers() {
        return workFlowConfig.workflowDefaultCopy();
    }

    @Override
    public WorkflowBase findUserTodoLatest1ByProcessDefinitionKeys(String userid, List<String> keys) {
        String keyIn = keys.stream().map(item -> "'" + item + "'").collect(Collectors.joining(", "));
        final List<Task> list = taskService.createNativeTaskQuery()
                .sql("SELECT * FROM ACT_RU_TASK t " +
                        "left join ACT_RE_PROCDEF D on t.PROC_DEF_ID_ = D.ID_  " +
                        "left join ACT_RU_IDENTITYLINK i on t.ID_ = i.TASK_ID_ " +
                        "WHERE t.TASK_DEF_KEY_ NOT LIKE '%apply%' " +
                        "and (t.ASSIGNEE_ = #{userid} or (t.ASSIGNEE_ is null and i.USER_ID_ = #{userid})) " +
                        "and t.SUSPENSION_STATE_ = 1 " +
                        "and D.KEY_ in (" + keyIn + ") " +
                        "order by t.CREATE_TIME_ desc;"
                )
                .parameter("userid", userid)
                .list();
        //去掉申请节点
        if (!CollectionUtils.isEmpty(list)) {
            //1. 获取对应的业务实体
            Task task = list.get(0);
            String procId = task.getProcessInstanceId();
            final HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procId).singleResult();
            final VariableInstance variableInstance = runtimeService.createVariableInstanceQuery().processInstanceIdIn(procId).variableName(Constants.VAR_KEY_ENTITY).singleResult();
            if (variableInstance == null) {
                //临时处理，存在null情况
                return null;
            }
            final String entityId = variableInstance.getValue().toString();
            BusinessService businessService = serviceMap.get(historicProcessInstance.getProcessDefinitionKey());
            final WorkflowBase load = businessService.load(entityId);
            return load;
        }
        return null;
    }

    @Override
    public long countUserFinanceTodo(String userid) {
        return countUserTodo(userid, WorkFlowConfig.financeWorkflowDef);
    }

    @Override
    public long countUserTodo(String userid, List<String> keys) {
        String keyIn = keys.stream().map(item -> "'" + item + "'").collect(Collectors.joining(", "));
        final long count = taskService.createNativeTaskQuery()
                .sql("SELECT count(1) FROM ACT_RU_TASK t " +
                        "left join ACT_RE_PROCDEF D on t.PROC_DEF_ID_ = D.ID_  " +
                        "left join ACT_RU_IDENTITYLINK i on t.ID_ = i.TASK_ID_ " +
                        "WHERE t.TASK_DEF_KEY_ NOT LIKE '%apply%' " +
                        "and (t.ASSIGNEE_ = #{userid} or (t.ASSIGNEE_ is null and i.USER_ID_ = #{userid})) " +
                        "and t.SUSPENSION_STATE_ = 1 " +
                        "and D.KEY_ in (" + keyIn + "); "
                )
                .parameter("userid", userid)
                .count();
        return count;
    }


    @Override
    public void deleteProcessInstance(String processInstanceId, String deleteReason) {
        WorkFlowUtil.ensureProcessId(processInstanceId);
        WorkFlowUtil.ensureProperty(deleteReason, "deleteReason(流程删除原因)");
        runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
    }

}
