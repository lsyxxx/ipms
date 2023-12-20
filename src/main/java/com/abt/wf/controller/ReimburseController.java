package com.abt.wf.controller;

import com.abt.common.model.R;
import com.abt.common.model.User;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.FlowSetting;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseApplyForm;
import com.abt.wf.model.TaskDTO;
import com.abt.wf.serivce.ReimburseService;
import com.abt.wf.serivce.WorkFlowExecutionService;
import com.abt.wf.serivce.WorkFlowQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public R<List<TaskDTO>> previewFlow(@Validated @RequestBody ReimburseApplyForm form) {
//        getUserFromToken(form)
        //preview
        List<TaskDTO> previewList = workFlowExecutionService.previewFlow(form);
        //Necessary params: assigneeName, executeTime, taskName, comment,
        return R.success(previewList, previewList.size());
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
        workFlowExecutionService.approve(form);
        return R.success();
    }

    /**
     * 我申请的报销
     *
     * @param page     页数
     * @param size     单页条数
     * @param startDay 开始日期 yyyy-MM-dd
     */
    @GetMapping("/applylist")
    public R<List<TaskDTO>> myReimburseApplyList(@RequestParam("page") int page, @RequestParam("size") int size,
                                                 @RequestParam(value = "startDay", required = false) LocalDate startDay,
                                                 @RequestParam(value = "endDay", required = false) LocalDate endDay,
                                                 @RequestParam("userid") String userid) {
//        UserView userView = TokenUtil.getUserFromAuthToken();
        //code|cost|reason|rbsDate|state|currentTaskName|
//        String userid = "";
        String username = "";
        List<TaskDTO> list = workFlowQueryService.queryMyRbs(userid, startDay, endDay, page, size);
        return R.success(list, list.size(), size);
    }

    /**
     * 我的待办
     *
     */
    @GetMapping("/todo")
    public R<List<TaskDTO>> myReimburseTodoList(@RequestParam("page") int page, @RequestParam("size") int size,
                                                @RequestParam(value = "startDay", required = false) LocalDate startDay,
                                                @RequestParam(value = "endDay", required = false) LocalDate endDay,
                                                @RequestParam("userid") String userid) {
//        String userid = "";
        String username = "";
        List<TaskDTO> tasks = workFlowQueryService.queryMyTodoList(userid, startDay, endDay, page, size);
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
    public R<List<TaskDTO>> myTasksDone(@RequestParam("page") int page, @RequestParam("size") int size,
                                        @RequestParam(value = "startDay", required = false) LocalDate startDay,
                                        @RequestParam(value = "endDay", required = false) LocalDate endDay,
                                        @RequestParam("userid") String userid) {
        String username = "";
        final List<TaskDTO> tasks = workFlowQueryService.queryMyDoneList(userid, startDay, endDay, page, size);
        return R.success(tasks, tasks.size());
    }


    /**
     * 一个流程实例的执行记录
     *
     * @param processInstanceId
     * @return
     */
    @GetMapping("/log")
    public R<List<TaskDTO>> processInstanceLog(@RequestParam("processInstanceId") String processInstanceId,
                                               @RequestParam("userid") String userid) {
        final List<TaskDTO> taskDTOS = workFlowQueryService.queryProcessInstanceLog(processInstanceId, userid);
        return R.success(taskDTOS);
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
