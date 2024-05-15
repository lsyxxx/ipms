package com.abt.wf.controller;

import com.abt.common.model.R;
import com.abt.common.model.User;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.config.WorkFlowConfig;
import com.abt.wf.entity.WorkflowBase;
import com.abt.wf.model.TaskWrapper;
import com.abt.wf.service.ActivitiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/fin/todo/count")
    public R<Long> countMyFinanceTodoList() {
        UserView user = TokenUtil.getUserFromAuthToken();
        final long count = activitiService.countUserFinanceTodo(user.getId());
        return R.success(count);
    }

    /**
     * 默认抄送人
     * @return
     */
    @GetMapping("/defaultcc")
    public R<List<User>> getDefaultCopyUser() {
        final List<User> defaultCopyUsers = activitiService.findDefaultCopyUsers();
        return R.success(defaultCopyUsers, defaultCopyUsers.size());
    }

    /**
     * 获取用户最近的财务类待办事项
     */
    public void financeUserTodo() {
        UserView user = TokenUtil.getUserFromAuthToken();
    }

}
