package com.abt.wf.controller;

import com.abt.common.model.R;
import com.abt.common.model.User;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseApplyForm;
import com.abt.wf.model.TaskDTO;
import com.abt.wf.serivce.ReimburseService;
import com.abt.wf.serivce.WorkFlowExecutionService;
import com.abt.wf.serivce.WorkFlowQueryService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 报销
 */
@RestController
@Slf4j
@RequestMapping("/rbs")
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
    public R<List<HistoricTaskInstance>> previewFlow(@Validated @RequestBody ReimburseApplyForm form) {
//        getUserFromToken(form)
        //preview
        List<HistoricTaskInstance> previewList = workFlowExecutionService.previewFlow(form);
        //Necessary params: assigneeName, executeTime, taskName, comment,
        return R.success(previewList);
    }
    

    /**
     * 申请
     */
    @PostMapping("/apply")
    public R<String> apply(@Validated @RequestBody ReimburseApplyForm form) {
//        getUserFromToken(form);
        Reimburse reimburse = workFlowExecutionService.apply(form);
        return R.success(reimburse.getProcessInstanceId());
    }

    /**
     * 审批
     * @param form 申请表单
     */
    @PostMapping("/approve")
    public R approve(@RequestBody ReimburseApplyForm form) {
        //UserView userView = getUserFromAuthToken()
        workFlowExecutionService.apply(form);
        return R.success();
    }

    /**
     * 我申请的报销
     * @param page 页数
     * @param size 单页条数
     * @param startDay 开始日期 yyyy-MM-dd
     *
     */
    @GetMapping("/myrbs")
    public R<List<TaskDTO>> myReimburseApplyList(@RequestParam("page") int page, @RequestParam("size") int size,
                                                 @RequestParam("startDay") LocalDate startDay,
                                                 @RequestParam("endDay") LocalDate endDay) {
//        UserView userView = TokenUtil.getUserFromAuthToken();
        //code|cost|reason|rbsDate|state|currentTaskName|
        String userid = "";
        String username = "";
        List<TaskDTO> list = workFlowQueryService.queryMyRbs(userid, startDay, endDay, page, size);
        return R.success(list);
    }

    /**
     * 我的待办
     *
     */
    @GetMapping("/rbstodo")
    public R<List<TaskDTO>> myReimburseTodoList(@RequestParam("page") int page, @RequestParam("size") int size,
                                                @RequestParam("startDay") LocalDate startDay,
                                                @RequestParam("endDay") LocalDate endDay) {
        String userid = "";
        String username = "";
        List<TaskDTO> tasks = workFlowQueryService.queryTaskListByStartUserid(userid, startDay, endDay, page, size);
        return R.success(tasks);
    }



    private User testUser1() {
        User user = new User();
        user.setId("abttest");
        user.setUsername("刘宋菀");
        user.setCode("abttest");
        return user;
    }

    public ReimburseApplyForm getUserFromToken(ReimburseApplyForm form) {
        UserView userView = TokenUtil.getUserFromAuthToken();
        form.setUserid(userView.getId());
        form.setUsername(userView.getUsername());
        return form;
    }



}
