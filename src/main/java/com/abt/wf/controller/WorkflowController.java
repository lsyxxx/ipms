package com.abt.wf.controller;

import com.abt.common.model.Page;
import com.abt.common.model.R;
import com.abt.common.model.User;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.entity.WorkflowBase;
import com.abt.wf.model.ActivitiRequestForm;
import com.abt.wf.service.ActivitiService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通用
 */
@RestController
@Slf4j
@RequestMapping("/wf")
public class WorkflowController {
    private final ActivitiService activitiService;;


    public WorkflowController(ActivitiService activitiService) {
        this.activitiService = activitiService;
    }

    /**
     * 获取财务相关流程待办
     */
//    @GetMapping("/fin/todo/find1")
//    public R<WorkflowBase> getMyFinanceTodoList() {
//        UserView user = TokenUtil.getUserFromAuthToken();
//        final WorkflowBase financeTask = activitiService.findFinanceTask(user.getId());
//        return R.success(financeTask);
//    }

    /**
     * 默认抄送人
     */
    @GetMapping("/defaultcc")
    public R<List<User>> getDefaultCopyUser() {
        final List<User> defaultCopyUsers = activitiService.findDefaultCopyUsers();
        return R.success(defaultCopyUsers, defaultCopyUsers.size());
    }

    @GetMapping("/find/todo/all")
    public R<List<WorkflowBase>> findUserTodoAll(@RequestParam(required = false) String query, @RequestParam(required = false) String type) {
        final List<WorkflowBase> all = activitiService.findUserTodoAll(TokenUtil.getUseridFromAuthToken(), query, 0, 0);
        return R.success(all, all.size());
    }

    @GetMapping("/fin/todo/count")
    public R<Integer> findUserTodoCount() {
        final List<WorkflowBase> all = activitiService.findUserTodoAll(TokenUtil.getUseridFromAuthToken(), null, 0, 0);
        return R.success(all.size(), all.size());
    }

    /**
     * 获取用户最近的财务类待办事项
     */
    public void financeUserTodo() {
        UserView user = TokenUtil.getUserFromAuthToken();
    }

    /**
     * 删除流程
     * @param procId 流程实例id
     * @param deleteReason 删除原因
     */
    @GetMapping("/processinstance/del")
    public R<Object> deleteProcessInstance(String procId, String deleteReason) {
        activitiService.deleteProcessInstance(procId, deleteReason);
        return R.success("删除流程(" + procId + ")成功");
    }

    /**
     * 所有正在运行的流程节点
     */
    @GetMapping("/task/run/all")
    public R<List<Task>> runningTasks(@ModelAttribute ActivitiRequestForm requestForm) {
        final Page<Task> taskPage = activitiService.runningTasks(requestForm);
        return R.success(taskPage.getContent(), taskPage.getTotal());
    }


    /**
     * 已完成的流程
     */
    @GetMapping("/proc/hi")
    public R<List<HistoricProcessInstance>> findProcess(@ModelAttribute ActivitiRequestForm requestForm) {
        final Page<HistoricProcessInstance> list = activitiService.finishedProcess(requestForm);
        return R.success(list.getContent(), list.getTotal());
    }


    @GetMapping("/proc/rt")
    public R<List<ProcessInstance>> runtimeProcess(@ModelAttribute ActivitiRequestForm requestForm) {
        final Page<ProcessInstance> processInstancePage = activitiService.runtimeProcess(requestForm);
        return R.success(processInstancePage.getContent(), processInstancePage.getTotal());
    }
}
