package com.abt.wf.controller;

import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.config.WorkFlowConfig;
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
    @GetMapping("/fin/todo")
    public R<List<TaskWrapper>> getMyFinanceTodoList() {
        UserView user = TokenUtil.getUserFromAuthToken();
        List<String> keys = WorkFlowConfig.financeWorkflowDef;
        final List<TaskWrapper> financeTask = activitiService.findFinanceTask(user.getId(), keys.toArray(new String[0]));
        return R.success(financeTask, financeTask.size());
    }
}