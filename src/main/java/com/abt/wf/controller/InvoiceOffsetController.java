package com.abt.wf.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.finance.invoice.CheckAndSaveInvoice;
import com.abt.finance.service.InvoiceService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.InvoiceOffset;
import com.abt.wf.model.InvoiceOffsetRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.InvoiceOffsetService;
import com.abt.wf.util.WorkFlowUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.abt.wf.config.Constants.SERVICE_INV_OFFSET;

/**
 * 发票冲账申请
 */
@RestController
@Slf4j
@RequestMapping("/wf/invoffset")
public class InvoiceOffsetController {
    private final InvoiceOffsetService invoiceOffsetService;
    private final InvoiceService invoiceService;

    public InvoiceOffsetController(InvoiceOffsetService invoiceOffsetService, InvoiceService invoiceService) {
        this.invoiceOffsetService = invoiceOffsetService;
        this.invoiceService = invoiceService;
    }

    /**
     * 撤销一个流程
     * @param id: 撤销的流程的id
     */
    @GetMapping("/revoke")
    public R<Object> revoke(String id) {
        UserView user = TokenUtil.getUserFromAuthToken();
        invoiceOffsetService.revoke(id, user.getId(), user.getName());
        invoiceService.notUse(id, SERVICE_INV_OFFSET);
        return R.success("撤销成功");
    }

    /**
     * 重新提交
     */
    @GetMapping("/restart")
    public R<InvoiceOffset> restart(String id) {
        final InvoiceOffset copyEntity = invoiceOffsetService.getCopyEntity(id);
        return R.success(copyEntity);
    }


    @CheckAndSaveInvoice
    @PostMapping("/apply")
    public R<InvoiceOffset> apply(@Validated({ValidateGroup.Apply.class}) @RequestBody InvoiceOffset form) {
        form.generateInvoiceCode();
        final InvoiceOffset entity = invoiceOffsetService.apply(form);
        return R.success(entity);
    }

    @PostMapping("/approve")
    public R<Object> approve(@RequestBody InvoiceOffset form) {
        invoiceOffsetService.approve(form);
        if (WorkFlowUtil.isReject(form.getDecision())) {
            invoiceService.notUse(form.getId(), form.getServiceName());
        }
        return R.success("审批成功");
    }

    @GetMapping("/todo")
    public R<List<InvoiceOffset>> todoList(@ModelAttribute InvoiceOffsetRequestForm requestForm) {
        setTokenUser(requestForm);
        //criteria
        //申请人，申请时间，审批编号
        final Page<InvoiceOffset> page = invoiceOffsetService.findMyTodoByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/done")
    public R<List<InvoiceOffset>> doneList(InvoiceOffsetRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<InvoiceOffset> page = invoiceOffsetService.findMyDoneByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/myapply")
    public R<List<InvoiceOffset>> myApplyList(InvoiceOffsetRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<InvoiceOffset> page = invoiceOffsetService.findMyApplyByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/all")
    public R<List<InvoiceOffset>> all(@ModelAttribute InvoiceOffsetRequestForm requestForm) {
        final Page<InvoiceOffset> page = invoiceOffsetService.findAllByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/load/{id}")
    public R<InvoiceOffset> load(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException("审批编号不能为空!");
        }
        InvoiceOffset invoiceApply = invoiceOffsetService.getEntityWithCurrentTask(id);
        return R.success(invoiceApply);
    }

    @GetMapping("/del/{id}")
    public R<Object> delete(@PathVariable String id, @RequestParam(required = false) String reason) {
        invoiceOffsetService.delete(id, reason);
        invoiceService.deleteByRef(id, SERVICE_INV_OFFSET);
        return R.success("删除成功");
    }


    @PostMapping("/preview")
    public R<List<UserTaskDTO>> preview(@RequestBody InvoiceOffset form) {
        setSubmitUser(form);
        final List<UserTaskDTO> preview = invoiceOffsetService.preview(form);
        return R.success(preview, preview.size());
    }

    @GetMapping("/record/{id}")
    public R<List<FlowOperationLog>> processRecord(@PathVariable String id) {
        final List<FlowOperationLog> processRecord = invoiceOffsetService.processRecord(id, Constants.SERVICE_PAY);
        return R.success(processRecord, processRecord.size());
    }

    public void setTokenUser(InvoiceOffsetRequestForm form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setUserid(user.getId());
        form.setUsername(user.getName());
    }

    public void setSubmitUser(InvoiceOffset form) {
        form.setSubmitUserid(TokenUtil.getUserFromAuthToken().getId());
        form.setSubmitUsername(TokenUtil.getUserFromAuthToken().getName());
    }
}
