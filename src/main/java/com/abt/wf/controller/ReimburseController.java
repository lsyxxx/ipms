package com.abt.wf.controller;

import com.abt.common.model.RequestForm;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseForm;
import com.abt.wf.model.ReimburseRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.ReimburseService;
import com.abt.wf.service.impl.ReimburseServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.common.model.R;

import java.util.List;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/wf/rbs")
@Tag(name = "ReimburseController", description = "报销")
public class ReimburseController {

    private final ReimburseService reimburseService;

    public ReimburseController(ReimburseService reimburseService) {
        this.reimburseService = reimburseService;
    }

    /**
     * 获取业务流程数据
     * @param entityId 业务实体id
     */
    @GetMapping("/load/{entityId}")
    public R<Reimburse> load(@PathVariable String entityId) {
        final Reimburse rbs = reimburseService.load(entityId);
        return R.success(rbs);
    }

    @PostMapping("/apply")
    public R<Object> apply(@Validated @RequestBody ReimburseForm form) {
        getUserFromToken(form);
        setServiceName(form);
        form.setServiceName(ReimburseServiceImpl.SERVICE_NAME);
        reimburseService.apply(form);
        return R.success("流程申请成功");
    }

    @PostMapping("/approve")
    public R<Object> approve(@RequestBody ReimburseForm form) {
        getUserFromToken(form);
        setServiceName(form);
        reimburseService.approve(form);
        return R.success("审批成功");
    }

    /**
     * 撤销流程
     */
    @GetMapping("/revoke/{entityId}")
    public R<Object> revoke(@PathVariable String entityId) {
        reimburseService.revoke(entityId);
        return R.success("撤销成功");
    }

    @GetMapping("/delete/{entityId}")
    public R<Object> delete(@PathVariable String entityId) {
        reimburseService.delete(entityId);
        return R.success("删除成功");
    }

    @PostMapping("/record")
    public R<List<UserTaskDTO>> processRecord(ReimburseForm form) {
        getUserFromToken(form);
        final List<UserTaskDTO> processRecord = reimburseService.processRecord(form);
        return R.success(processRecord, processRecord.size());
    }

    /**
     * 所有报销记录
     */
    @GetMapping("/all")
    public void allList(@ModelAttribute ReimburseRequestForm queryForm) {
        //query: 分页, 审批编号, 审批结果，审批状态，创建人，创建时间
    }

    /**
     * 我已处理的
     */
    @GetMapping("/done")
    public void doneList() {

    }

    /**
     * 待我处理
     */
    @GetMapping("/todo")
    public void todoList() {

    }

    /**
     * 我申请的
     */
    public void myApply() {

    }

    @PostMapping("/preview")
    public R<List<UserTaskDTO>> preview(ReimburseForm form) {
        getUserFromToken(form);
        final List<UserTaskDTO> preview = reimburseService.preview(form);
        return R.success(preview, preview.size());
    }

    public void getUserFromToken(ReimburseForm form) {
        UserView userView = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(userView.getId());
        form.setSubmitUsername(userView.getUsername());
    }

    public void setServiceName(ReimburseForm form) {
        form.setServiceName(ReimburseServiceImpl.SERVICE_NAME);
    }
}
