package com.abt.wf.controller;

import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.FlowSetting;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ApprovalTask;
import com.abt.wf.model.ReimburseApplyForm;
import com.abt.wf.model.ReimburseDTO;
import com.abt.wf.model.TaskDTO;
import com.abt.wf.serivce.ReimburseService;
import com.abt.wf.serivce.WorkFlowExecutionService;
import com.abt.wf.serivce.WorkFlowQueryService;
import jakarta.servlet.ServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 报销
 */
@RestController
@Slf4j
@RequestMapping("/test/wf/rbs")
public class ReimburseController {

    private final WorkFlowExecutionService workFlowExecutionService;
    private final WorkFlowQueryService workFlowQueryService;
    private final ReimburseService reimburseService;

    public ReimburseController(WorkFlowExecutionService workFlowExecutionService, WorkFlowQueryService workFlowQueryService, ReimburseService reimburseService) {
        this.workFlowExecutionService = workFlowExecutionService;
        this.workFlowQueryService = workFlowQueryService;
        this.reimburseService = reimburseService;
    }

    @PostMapping("/preview")
    public R<List<ApprovalTask>> previewFlow(@RequestBody ReimburseApplyForm rbsApplyForm) {
        getUserFromToken(rbsApplyForm);
        if (rbsApplyForm.getCost() < 0) {
            return R.fail("报销金额必填，且不能小于0");
        }
        //preview
        final String previewId = workFlowExecutionService.previewFlow(rbsApplyForm);
        final List<ApprovalTask> previewList = workFlowQueryService.queryProcessInstanceLog(previewId);

        //Necessary params: assigneeName, executeTime, taskName, comment,
        return R.success(previewList, previewList.size());
    }
    

    /**
     * 申请
     */
    @PostMapping("/apply")
    public R<String> apply(@Validated @RequestBody ReimburseApplyForm form) {
        getUserFromToken(form);
        if (form.getRbsDate() == null) {
            form.setRbsDate(LocalDateTime.now());
        }
        Reimburse reimburse = workFlowExecutionService.apply(form);
        return R.success(reimburse.getProcessInstanceId(), 0);
    }

    /**
     * 审批
     * @param form 申请表单
     */
    @PostMapping("/approve")
    public R approve(@RequestBody ReimburseApplyForm form) {
        getUserFromToken(form);
        workFlowExecutionService.approve(form);
        return R.success();
    }

    /**
     * 我申请的报销
     *
     * @param page     页数
     * @param limit    单页条数
     * @param startDay 开始日期 yyyy-MM-dd
     */
    @GetMapping("/applylist")
    public R<List<ReimburseDTO>> myReimburseApplyList(@RequestParam("page") int page, @RequestParam("limit") int limit,
                                                      @RequestParam(value = "startDay", required = false) LocalDate startDay,
                                                      @RequestParam(value = "endDay", required = false) LocalDate endDay) {
        UserView userView = TokenUtil.getUserFromAuthToken();
        //code|cost|reason|rbsDate|state|currentTaskName|
        List<ReimburseDTO> list = workFlowQueryService.queryMyRbs(userView.getId(), startDay, endDay, page, limit);
        return R.success(list, list.size(), limit);
    }

    /**
     * 我的待办
     *
     */
    @GetMapping("/todo")
    public R<List<TaskDTO>> myReimburseTodoList(@RequestParam("page") int page, @RequestParam("limit") int limit,
                                                @RequestParam(value = "startDay", required = false) LocalDate startDay,
                                                @RequestParam(value = "endDay", required = false) LocalDate endDay) {
        UserView userView = TokenUtil.getUserFromAuthToken();
        List<TaskDTO> tasks = workFlowQueryService.queryMyTodoList(userView.getId(), startDay, endDay, page, limit);
        return R.success(tasks, tasks.size());
    }

    @GetMapping("/types")
    public R<List<FlowSetting>> getRbsTypes() {
        final List<FlowSetting> rbsTypes = reimburseService.queryRbsTypes();
        return R.success(rbsTypes, rbsTypes.size());
    }

    /**
     * 已完成
     */
    @GetMapping("/done")
    public R<List<TaskDTO>> myTasksDone(@RequestParam("page") int page, @RequestParam("limit") int limit,
                                        @RequestParam(value = "startDay", required = false) LocalDate startDay,
                                        @RequestParam(value = "endDay", required = false) LocalDate endDay) {
        UserView userView = TokenUtil.getUserFromAuthToken();
        final List<TaskDTO> tasks = workFlowQueryService.queryMyDoneList(userView.getId(), startDay, endDay, page, limit);
        return R.success(tasks, tasks.size());
    }


    /**
     * 一个流程实例的执行记录
     * @param processInstanceId 流程实例
     */
    @GetMapping("/log")
    public R<List<ApprovalTask>> processInstanceLog(@RequestParam("processInstanceId") String processInstanceId) {
        UserView userView = TokenUtil.getUserFromAuthToken();
        final List<ApprovalTask> list = workFlowQueryService.queryProcessInstanceLog(processInstanceId);
        return R.success(list);
    }

    public ReimburseApplyForm getUserFromToken(ReimburseApplyForm form) {
        UserView userView = TokenUtil.getUserFromAuthToken();
        form.setUserid(userView.getId());
        form.setUsername(userView.getUsername());
        return form;
    }
}
