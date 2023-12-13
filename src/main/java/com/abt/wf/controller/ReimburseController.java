package com.abt.wf.controller;

import com.abt.common.model.User;
import com.abt.wf.model.ReimburseApplyForm;
import com.abt.wf.serivce.WorkFlowExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 报销
 */
@RestController
@Slf4j
@RequestMapping("/rbs")
public class ReimburseController {

    private final WorkFlowExecutionService workFlowExecutionService;

    public ReimburseController(WorkFlowExecutionService workFlowExecutionService) {
        this.workFlowExecutionService = workFlowExecutionService;
    }

    @PostMapping("/preview")
    public void previewFlow(@Validated @RequestBody ReimburseApplyForm form) {
        //1. token validate
        //preview
        //List<HistoricTaskInstance> previewList = workFlowExecutionService.previewFlow(form)
        //Necessary params: assigneeName, executeTime, taskName, comment,
        //return VO OR List<HistoricTaskInstance> ?
    }
    

    /**
     * 申请
     */
    @PostMapping("/apply")
    public void apply(@Validated @RequestBody ReimburseApplyForm form) {
        //1. token validate
        //UserView userView = getUserFromAuthToken()
        //2. set token user to form
        //form.setUserid()
        //form.setUsername()
        User applyUser = testUser1();




    }

    private User testUser1() {
        User user = new User();
        user.setId("abttest");
        user.setUsername("刘宋菀");
        user.setCode("abttest");
        return user;
    }

}
