package com.abt.wf.controller;

import com.abt.common.model.User;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.model.ReimburseApplyForm;
import com.abt.wf.serivce.ReimburseService;
import com.abt.wf.serivce.WorkFlowExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报销
 */
@RestController
@Slf4j
@RequestMapping("/rbs")
public class ReimburseController {

    private final WorkFlowExecutionService workFlowExecutionService;
    private final ReimburseService reimburseService;

    public ReimburseController(WorkFlowExecutionService workFlowExecutionService, ReimburseService reimburseService) {
        this.workFlowExecutionService = workFlowExecutionService;
        this.reimburseService = reimburseService;
    }

    @PostMapping("/preview")
    public void previewFlow(@Validated @RequestBody ReimburseApplyForm form) {
//        getUserFromToken(form)
        //preview
        List<HistoricTaskInstance> previewList = workFlowExecutionService.previewFlow(form);
        //Necessary params: assigneeName, executeTime, taskName, comment,
        //TODO
        //return VO OR List<HistoricTaskInstance> ?
    }
    

    /**
     * 申请
     */
    @PostMapping("/apply")
    public void apply(@Validated @RequestBody ReimburseApplyForm form) {
//        getUserFromToken(form);
        workFlowExecutionService.apply(form);
        reimburseService.saveEntity(form);
        //TODO: ?
//        return ?
    }

    /**
     * 审批
     * @param form 申请表单
     */
    @PostMapping("/approve")
    public void approve(@RequestBody ReimburseApplyForm form) {
        //UserView userView = getUserFromAuthToken()


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
