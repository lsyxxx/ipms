package com.abt.flow.controller;

import com.abt.common.model.R;
import com.abt.common.util.MessageUtil;
import com.abt.common.util.TokenUtil;
import com.abt.flow.model.FlowInfoVo;
import com.abt.flow.model.FlowRequestForm;
import com.abt.flow.model.ReimburseApplyForm;
import com.abt.flow.service.FlowEntry;
import com.abt.flow.service.FlowInfoService;
import com.abt.flow.service.ReimburseService;
import com.abt.sys.model.dto.UserView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;

/**
 * 报销流程
 */
@RestController
@Slf4j
@RequestMapping("/wf/rb")
@Tag(name = "ReimburseController", description = "报销流程")
public class ReimburseController {

    @Value("com.abt.flow.controller.ReimburseController.type")
    private String reimburseType;

    public final static String SERVICE = "wf_reimburse";

    private final ReimburseService reimburseService;
    private final FlowInfoService flowInfoService;

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();

    public ReimburseController(ReimburseService reimburseService, FlowInfoService flowInfoService) {
        this.reimburseService = reimburseService;
        this.flowInfoService = flowInfoService;
    }



    @Operation(summary = "报销流程申请")
    @Parameter(name = "applyForm", description = "申请业务数据form")
    @PostMapping("/apply")
    public R apply(@RequestBody ReimburseApplyForm applyForm) {
        UserView user = TokenUtil.getUserFromAuthToken();

        reimburseService.apply(user, applyForm);

        return R.success(messages.getMessage("common.apply.success"));
    }

    @Operation(summary = "部门审批")
    @Parameter(name = "applyForm", description = "业务数据form")
    @PostMapping("/dpt")
    public R departmentAudit(@RequestBody @NotNull ReimburseApplyForm applyForm) {
        UserView user = TokenUtil.getUserFromAuthToken();

        reimburseService.departmentAudit(user, applyForm);

        return R.success();
    }


    @Operation(summary = "技术负责人审批")
    @Parameter(name = "applyForm", description = "业务数据form")
    @PostMapping("/tch")
    public R techAudit(@RequestBody @NotNull ReimburseApplyForm applyForm) {
        UserView user = TokenUtil.getUserFromAuthToken();

        reimburseService.techLeadAudit(user, applyForm);

        return R.success();
    }


    @Operation(summary = "财务主管审批")
    @Parameter(name = "applyForm", description = "业务数据form")
    @PostMapping("/fin")
    public R financeManagerAudit(@RequestBody @NotNull ReimburseApplyForm applyForm) {
        UserView user = TokenUtil.getUserFromAuthToken();

        reimburseService.financeManagerAudit(user, applyForm);

        return R.success();
    }


    @Operation(summary = "总经理审批")
    @Parameter(name = "applyForm", description = "业务数据form")
    @PostMapping("/ceo")
    public R ceoAudit(@RequestBody @NotNull ReimburseApplyForm applyForm) {
        UserView user = TokenUtil.getUserFromAuthToken();

        reimburseService.ceoAudit(user, applyForm);

        return R.success();
    }


    @Operation(summary = "税务会计审批")
    @Parameter(name = "applyForm", description = "业务数据form")
    @PostMapping("/tax")
    public R taxOfficerAudit(@RequestBody @NotNull ReimburseApplyForm applyForm) {
        UserView user = TokenUtil.getUserFromAuthToken();

        reimburseService.taxOfficerAudit(user, applyForm);

        return R.success();
    }

    @Operation(summary = "财务会计审批")
    @Parameter(name = "applyForm", description = "业务数据form")
    @PostMapping("/act")
    public R accountantAudit(@RequestBody @NotNull ReimburseApplyForm applyForm) {
        UserView user = TokenUtil.getUserFromAuthToken();

        reimburseService.accountantAudit(user, applyForm);

        return R.success();
    }


    @Operation(summary = "我的报销记录")
    @Parameter(name = "form", description = "查询条件表单(审批状态，结果)")
    @PostMapping("/myrbs")
    public R myReimburse(@RequestParam FlowRequestForm form) {

        UserView user = TokenUtil.getUserFromAuthToken();
        //TODO: 报销的type, 目前FlowScheme中的id
//        form.setType("7c2f7166-4696-4f4f-bf59-c4447179cbe3");
        form.setType(reimburseType);
        form.setUser(user);

        List<FlowInfoVo> list = flowInfoService.getUserApplyFlows(form);

        return R.success(list, list.size());
    }


    @Operation(summary = "流程图")
    @Parameter(name = "applyForm", description = "业务数据form")
    @PostMapping("/diag")
    public ResponseEntity<InputStreamResource> PngDiagram(@RequestParam ReimburseApplyForm applyForm) {
        UserView user = TokenUtil.getUserFromAuthToken();

        InputStream highLightedTaskPngDiagram = reimburseService.getHighLightedTaskPngDiagram(user, applyForm);
        InputStreamResource resource = new InputStreamResource(highLightedTaskPngDiagram);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // 设置图像类型为JPEG
                .body(resource);
    }

    @Operation(summary = "获取流程附件")
    @Parameter(name = "procId", description = "流程实例id")
    @GetMapping("/files")
    public void getAttachmentList(String procId) {

    }



    @Operation(summary = "删除流程")
    @Parameter(name = "procId", description = "流程实例id")
    @GetMapping("/del")
    public R delete(String procId) {

        reimburseService.delete(procId);

        return R.success();
    }


    @Operation(summary = "撤销流程")
    @Parameter(name = "id", description = "报销业务id")
    @GetMapping("/cancel")
    public R cancel(String id) {

        return R.success();
    }

}
