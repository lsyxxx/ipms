package com.abt.wfbak.controller;

import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.FlowSetting;
import com.abt.wfbak.entity.Reimburse;
import com.abt.wfbak.model.ApprovalTask;
import com.abt.wfbak.model.ReimburseForm;
import com.abt.wfbak.model.TaskDTO;
import com.abt.wfbak.service.ReimburseService;
import com.abt.wfbak.service.WorkFlowExecutionService;
import com.abt.wfbak.service.WorkFlowQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 报销
 */
@RestController
@Slf4j
@RequestMapping("/wf/rbs")
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
    public R<List<ApprovalTask>> previewFlow( @RequestBody ReimburseForm form) {
        getUserFromToken(form);
        if (form.getCost() < 0) {
            return R.fail("报销金额必填，且不能小于0");
        }
        //preview
        final List<ApprovalTask> previewList = workFlowExecutionService.previewFlow(form);
        return R.success(previewList, previewList.size());
    }

    /**
     * 读取流程数据
     */
    @PostMapping("/load")
    public R<ReimburseForm> load(@RequestParam(required = false) String entityId) {
        ReimburseForm dto = new ReimburseForm();
        if (StringUtils.isNotBlank(entityId)) {
            dto = reimburseService.loadReimburse(entityId);
        }
        return R.success(dto);
    }
    

    /**
     * 申请
     */
    @PostMapping("/apply")
    public R<String> apply(@Validated @RequestBody ReimburseForm form) {
        getUserFromToken(form);
        if (form.getRbsDate() == null) {
            form.setRbsDate(LocalDate.now());
        }
        Reimburse reimburse = workFlowExecutionService.apply(form);
        return R.success(reimburse.getProcessInstanceId(), 0);
    }

    /**
     * 审批
     * @param form 申请表单
     */
    @PostMapping("/approve")
    public R approve(@RequestBody ReimburseForm form) {
        getUserFromToken(form);
        workFlowExecutionService.approve(form);
        return R.success();
    }

    @GetMapping("/approve2")
    public R approve(String decision, String id,
                     @RequestParam(required = false) String comment) {
        final ReimburseForm reimburseForm = reimburseService.loadReimburse(id);
        getUserFromToken(reimburseForm);
        reimburseForm.setDecision(decision);
        reimburseForm.setComment(comment);
        workFlowExecutionService.approve(reimburseForm);
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
    public R<List<ReimburseForm>> myReimburseApplyList(@RequestParam("page") int page, @RequestParam("limit") int limit,
                                                      @RequestParam(value = "startDay", required = false) LocalDate startDay,
                                                      @RequestParam(value = "endDay", required = false) LocalDate endDay) {
        //前端是从1开始，jpa分页从0开始
        page = page - 1;
        UserView userView = TokenUtil.getUserFromAuthToken();
        //code|cost|reason|rbsDate|state|currentTaskName|
        List<ReimburseForm> list = workFlowQueryService.queryMyRbs(userView.getId(), startDay, endDay, page, limit);
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
     * 删除流程
     * @param processId 流程id
     */
    @GetMapping("/del/{processId}")
    public R<Object> deleteProcess(@PathVariable String processId) {
        UserView userView = TokenUtil.getUserFromAuthToken();

        return R.success();
    }


    /**
     * 一个流程实例的执行记录
     * @param processInstanceId 流程实例
     */
    @GetMapping("/log")
    public R<List<ApprovalTask>> processInstanceLog(@RequestParam("processInstanceId") String processInstanceId,
                                                    @RequestParam(value = "isFinished", required = false, defaultValue = "false") Boolean isFinished,
                                                    @RequestParam(value = "entityId") String entityId) {
//        ReimburseForm entity = reimburseService.loadReimburse(entityId);
////        ReimburseForm form = ReimburseForm
//        List<ApprovalTask> list = workFlowQueryService.queryProcessInstanceLog(processInstanceId);
//        if (!isFinished) {
////            final List<ApprovalTask> previewList = workFlowExecutionService.previewFlow(rbsApplyForm);
//
//        }
//        return R.success(list);
        return R.success();
    }

    public ReimburseForm getUserFromToken(ReimburseForm form) {
        UserView userView = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(userView.getId());
        form.setSubmitUsername(userView.getUsername());
        return form;
    }
}
