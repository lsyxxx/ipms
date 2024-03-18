package com.abt.wf.controller;

import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseForm;
import com.abt.wf.service.ReimburseService;
import com.abt.wf.service.impl.ReimburseServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.common.model.R;

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

    public void preview(ReimburseForm form) {

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
