package com.abt.wf.service.impl;

import com.abt.common.model.ResCode;
import com.abt.common.model.User;
import com.abt.common.util.MessageUtil;
import com.abt.common.util.TimeUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.exception.NoSuchActiveTaskException;
import com.abt.wf.model.CommentForm;
import com.abt.wf.model.ReimburseApplyForm;
import com.abt.wf.service.WorkFlowService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程服务
 */
@Service
@Slf4j
public class WorkFlowServiceImpl implements WorkFlowService {

    private final RuntimeService runtimeService;
    private final HistoryService historyService;

    private final TaskService taskService;

    /**
     * 领导
     */
    private final Map<String, User> flowSkipManagerMap;

    public WorkFlowServiceImpl(RuntimeService runtimeService, HistoryService historyService, TaskService taskService, Map<String, User> flowSkipManagerMap) {
        this.runtimeService = runtimeService;
        this.historyService = historyService;
        this.taskService = taskService;
        this.flowSkipManagerMap = flowSkipManagerMap;
    }


    public void apply(ReimburseApplyForm form, UserView user) {
        log.info("流程申请开始, 申请人-- id: {}, name: {}; 流程定义key: {}", user.getId(), user.getName(), form.getProcessDefKey());
        //1. prepare
        form.setStarter(user.getId());
        Map<String, Object> vars = prepareVars(form);
        //2. start
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(form.getProcessDefId(), rbsBizKey(user.getId()), vars);
        //3. apply task
        Task activeTask = getSingleActiveTask(processInstance.getId());
        taskService.complete(activeTask.getId());
        log.info("流程申请结束");
    }

    public String approve(CommentForm form, UserView user) {
        log.info("流程审批开始, 审批人 -- id: {}, name: {}, 流程实例id: {}", user.getId(), user.getName(), form.getProcessInstanceId());
        Task activeTask = getSingleActiveTask(form.getProcessInstanceId());

        if (activeTask == null) {
            log.error("未查询到活动流程节点. processInstanceId: {}", form.getProcessInstanceId());
            throw new NoSuchActiveTaskException(MessageUtil.getMessage("common.flow.noActiveTask"));
        }

        taskService.createComment(activeTask.getId(), form.getProcessInstanceId(), form.getComment());

        if (form.isReject()) {
            //审批拒绝 TODO: 暂时delete，deleteReason: 审批拒绝+userid+节点名称
            runtimeService.deleteProcessInstance(form.getProcessInstanceId(), "审批拒绝," + user.getId() + "," + activeTask.getName());
        }

        taskService.complete(activeTask.getId());

        return ResCode.SUCCESS.getMessage();

    }

    /**
     * 报销业务流程key
     * rbs+ts+userid
     */
    private String rbsBizKey(String applyUser) {
        return "rbs-" + TimeUtil.idGenerator() + "-" + applyUser;
    }

    /**
     * 准备数据
     */
    private Map<String, Object> prepareVars(ReimburseApplyForm form) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("isLeader", isLeader(form.getStarter()));
        vars.put("cost", form.getCost());

        return vars;
    }

    private boolean isLeader(String userId) {
        return flowSkipManagerMap.containsKey(userId);
    }


    private Task getSingleActiveTask(String procInstId) {
        return taskService.createTaskQuery().active().processInstanceId(procInstId).singleResult();
    }

}
