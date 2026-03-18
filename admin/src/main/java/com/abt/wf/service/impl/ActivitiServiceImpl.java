package com.abt.wf.service.impl;

import com.abt.common.model.Page;
import com.abt.common.model.Pair;
import com.abt.common.model.RequestForm;
import com.abt.common.model.User;
import com.abt.common.util.QueryUtil;
import com.abt.wf.config.WorkFlowConfig;
import com.abt.wf.entity.WorkflowBase;
import com.abt.wf.model.*;
import com.abt.wf.service.*;
import com.abt.wf.util.WorkFlowUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstanceQuery;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    private final Map<String, BusinessService<? extends RequestForm, ? extends WorkflowBase>> serviceMap;

    private final ReimburseService reimburseService;
    private final TripService tripService;
    private final InvoiceApplyService invoiceApplyService;
    private final InvoiceOffsetService invoiceOffsetService;
    private final LoanService loanService;
    private final PayVoucherService payVoucherService;
    private final EntityManager entityManager;

    public ActivitiServiceImpl(WorkFlowConfig workFlowConfig, TaskService taskService, HistoryService historyService, RuntimeService runtimeService,
                               Map<String, BusinessService<? extends RequestForm, ? extends WorkflowBase>> serviceMap,
                               ReimburseService reimburseService, TripService tripService, InvoiceApplyService invoiceApplyService, InvoiceOffsetService invoiceOffsetService, LoanService loanService, PayVoucherService payVoucherService, EntityManager entityManager) {
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
        this.entityManager = entityManager;
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
    public List<User> findDefaultCopyUsers() {
        return List.of();
    }


    //查询所有的待办流程
    @Override
    public UserTodo countTodoAll(String activeKey, String userid) {
        UserTodo userTodo = new UserTodo();
        serviceMap.forEach((key, service) -> {
            RequestForm form = service.createRequestForm();
            form.setPage(1);
            form.setLimit(9999);
            form.setUserid(userid);
            //查询用户在这个流程担任了几个task
            final List<Pair> names = findUserTaskName(userid, key);
            if (names.size() > 1) {
                //有1个以上显示不同职责
                names.forEach(n -> {
                    form.setTaskDefKey(n.getKey().toString());
                    int count = service.countMyTodoByRequestForm(form);
                    userTodo.addTodoCount(key + "|" + n.getValue(), count);
                    userTodo.accumulateCount(count);
                });
            } else {
                //只有1个职责则不显示
                int count = service.countMyTodoByRequestForm(form);
                userTodo.addTodoCount(key, count);
                userTodo.accumulateCount(count);
            }
            if (key.equals(activeKey)) {
                final List<? extends WorkflowBase> list = service.findMyTodoList(form);
                userTodo.setActiveTodoList(Collections.singletonList(list));
                userTodo.setActiveKey(key);
            }
        });
        userTodo.addTodoCount("all", userTodo.getTodoCountAll());
        return userTodo;
    }

    @Override
    public List<Object> findTodoByDefKey(String defKey, String taskName, String query, String userid) {
        List<Object> list = new ArrayList<>();
        if (defKey.contains("all")) {
            serviceMap.forEach((key, service) -> {
                RequestForm form = service.createRequestForm();
                form.setPage(1);
                form.setLimit(9999);
                form.setUserid(userid);
                form.setQuery(query);
                final List<? extends WorkflowBase> defList = service.findMyTodoList(form);
                final List<? extends WorkflowBase> filtered = defList.stream()
                        .filter(i -> StringUtils.isBlank(taskName) || taskName.equals(i.getCurrentTaskName())).toList();
                list.addAll(filtered);
            });
            return list;
        } else {
            final BusinessService<? extends RequestForm, ? extends WorkflowBase> service = serviceMap.get(defKey);
            if (service == null) {
                return null;
            }
            RequestForm form = service.createRequestForm();
            form.setPage(1);
            form.setLimit(9999);
            form.setUserid(userid);
            form.setQuery(query);
            final List<? extends WorkflowBase> defList = service.findMyTodoList(form);
            final List<? extends WorkflowBase> filtered = defList.stream()
                    .filter(i -> StringUtils.isBlank(taskName) || taskName.equals(i.getCurrentTaskName())).toList();
            list.addAll(filtered);
            return list;
        }
    }

    @Override
    public void deleteProcessInstance(String processInstanceId, String deleteReason) {
        WorkFlowUtil.ensureProcessId(processInstanceId);
        WorkFlowUtil.ensureProperty(deleteReason, "deleteReason(流程删除原因)");
        runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
    }


    //待处理任务
    @Override
    public Page<Task> runningTasks(ActivitiRequestForm form) {
        final List<Task> tasks = taskService.createTaskQuery().active().orderByTaskCreateTime().desc().listPage(form.getPage(), form.getLimit());
        final long count = taskService.createTaskQuery().active().count();
        return new Page<Task>(tasks, (int)count);
    }


    @Override
    public Page<HistoricProcessInstance> finishedProcess(ActivitiRequestForm form) {
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
        if (form.getIsFinished() != null && form.getIsFinished()) {
            query = query.finished();
        }
        final List<HistoricProcessInstance> list = query.orderByProcessInstanceStartTime().desc().listPage(form.getPage(), form.getLimit());
        final long count = query.count();
        return new Page<>(list, (int)count);
    }


    @Override
    public Page<ProcessInstance> runtimeProcess(ActivitiRequestForm form) {
        final ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery().active();
        if (StringUtils.isNotBlank(form.getProcessInstanceId())) {
            query.processInstanceId(form.getProcessInstanceId());
        }
        final List<ProcessInstance> list = query.listPage(form.getPage(), form.getLimit());
        final long count = query.count();
        return new Page<ProcessInstance>(list, (int)count);
    }

    public List<Pair> findUserTaskName(String userid, String procDefKey) {
        final Query query = entityManager.createNativeQuery("select distinct(TASK_DEF_KEY_), NAME_ from ACT_RU_TASK where ASSIGNEE_ = :userid and PROC_DEF_ID_ LIKE :procDefIdLike");
        query.setParameter("userid", userid);
        query.setParameter("procDefIdLike", QueryUtil.like(procDefKey));
        final List<Object[]> resultList = query.getResultList();
        if (CollectionUtils.isEmpty(resultList)) {
            return List.of();
        } else {
            List<Pair> result = new ArrayList<>();
            for (Object[] row : resultList) {
                Pair pair = new Pair(row[0], row[1]);
                result.add(pair);
            }
            return result;
        }
    }


}
