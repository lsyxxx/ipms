package com.abt.wf.controller;

import com.abt.common.model.R;
import com.abt.common.model.User;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.config.WorkFlowConfig;
import com.abt.wf.entity.Loan;
import com.abt.wf.entity.WorkflowBase;
import com.abt.wf.model.TaskWrapper;
import com.abt.wf.service.ActivitiService;
import com.abt.wf.service.WorkFlowService;
import lombok.extern.slf4j.Slf4j;
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
    @GetMapping("/fin/todo/find1")
    public R<WorkflowBase> getMyFinanceTodoList() {
        UserView user = TokenUtil.getUserFromAuthToken();
        final WorkflowBase financeTask = activitiService.findFinanceTask(user.getId());
        return R.success(financeTask);
    }

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
    public void deleteProcessInstance(String procId, String deleteReason) {
        activitiService.deleteProcessInstance(procId, deleteReason);
    }
}
