package com.abt.wf.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.util.JsonUtil;
import com.abt.common.util.TokenUtil;
import com.abt.finance.entity.Invoice;
import com.abt.finance.invoice.CheckAndSaveInvoice;
import com.abt.finance.service.InvoiceService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.PayVoucher;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.entity.TripMain;
import com.abt.wf.model.PayVoucherRequestForm;
import com.abt.wf.model.ReimburseExportDTO;
import com.abt.wf.model.ReimburseRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.CostDetailService;
import com.abt.wf.service.PayVoucherService;
import cn.idev.excel.EasyExcel;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.write.metadata.WriteSheet;
import com.abt.wf.service.ReimburseService;
import com.abt.wf.util.WorkFlowUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.abt.wf.config.Constants.SERVICE_PAY;
import static com.abt.wf.config.Constants.SERVICE_RBS;

/**
 * 款项支付单
 */
@RestController
@Slf4j
@RequestMapping("/wf/pay")
public class PayVoucherController {
    private final PayVoucherService payVoucherService;
    private final CostDetailService costDetailService;
    private final InvoiceService invoiceService;

    @Value("${abt.pay.excel.template}")
    private String excelTemplate;

    @Value("${abt.rbs.excel.dtl.template}")
    private String detailTemplate;

    /**
     *  临时下载保存地址
     */
    @Value("${abt.temp.dir}")
    private String tempDir;

    public PayVoucherController(PayVoucherService payVoucherService, CostDetailService costDetailService, InvoiceService invoiceService) {
        this.payVoucherService = payVoucherService;
        this.costDetailService = costDetailService;
        this.invoiceService = invoiceService;
    }

    /**
     * 转交任务
     */
    @GetMapping("/task/delegate")
    public R<Object> delegateTask(String entityId, String comment, String decision, String toUserid) {
        final PayVoucher entity = payVoucherService.load(entityId);
        setSubmitUser(entity);
        entity.setComment(comment);
        entity.setDecision(decision);
        entity.setDelegateUser(toUserid);
        payVoucherService.delegateTask(entity, entity.getDelegateUser(), entity.getComment(), entity.getDecision());
        return R.success(String.format("已将任务转交给%s", entity.getDelegateUser()));
    }

    /**
     * 处理转办任务
     */
    @GetMapping("/task/resolve")
    public R<Object> resolveTask(String entityId, String comment, String decision) {
        final PayVoucher form = payVoucherService.load(entityId);
        setSubmitUser(form);
        payVoucherService.resolveTask(form, comment, decision);
        return R.success("已处理转办任务");
    }

    /**
     * 撤销一个流程
     * @param id: 撤销的流程的id
     */
    @GetMapping("/revoke")
    public R<Object> revoke(String id) {
        UserView user = TokenUtil.getUserFromAuthToken();
        payVoucherService.revoke(id, user.getId(), user.getName());
        invoiceService.notUse(id, SERVICE_PAY);
        return R.success("撤销成功");
    }

    @GetMapping("/restart")
    public R<PayVoucher> copyEntity(String id) {
        final PayVoucher copyEntity = payVoucherService.getCopyEntity(id);
        return R.success(copyEntity);
    }

    @CheckAndSaveInvoice
    @PostMapping("/apply")
    public R<Object> apply(@Validated(ValidateGroup.Apply.class) @RequestBody PayVoucher payVoucher) {
        setSubmitUser(payVoucher);
        costDetailService.check(payVoucher.getCostDetailList(), payVoucher.getPayAmount().doubleValue());
        final PayVoucher entity = payVoucherService.apply(payVoucher);
        costDetailService.save(payVoucher.getCostDetailList(), entity.getId());
        return R.success(entity);
    }

    @PostMapping("/approve")
    public R<Object> approve(@RequestBody PayVoucher payVoucher) {
        setSubmitUser(payVoucher);
        payVoucherService.approve(payVoucher);
        if (WorkFlowUtil.isReject(payVoucher.getDecision())) {
            invoiceService.notUse(payVoucher.getId(), payVoucher.getServiceName());
        }
        return R.success("审批成功");
    }

    @GetMapping("/todo")
    public R<List<PayVoucher>> todoList(@ModelAttribute PayVoucherRequestForm requestForm) {
        setUser(requestForm);
        final Page<PayVoucher> page = payVoucherService.findMyTodoByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/done")
    public R<List<PayVoucher>> doneList(@ModelAttribute PayVoucherRequestForm requestForm) {
        setUser(requestForm);
        final Page<PayVoucher> page = payVoucherService.findMyDoneByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/myapply")
    public R<List<PayVoucher>> myApplyList(@ModelAttribute PayVoucherRequestForm requestForm) {
        setUser(requestForm);
        final Page<PayVoucher> page = payVoucherService.findMyApplyByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/all")
    public R<List<PayVoucher>> all(@ModelAttribute PayVoucherRequestForm requestForm) {
        setUser(requestForm);
        final Page<PayVoucher> page = payVoucherService.findAllByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/load/{id}")
    public R<PayVoucher> load(@PathVariable String id) {
        final PayVoucher payVoucher = payVoucherService.loadEntityWithCurrentTask(id);
        return R.success(payVoucher);
    }

    @GetMapping("/del/{id}")
    public R<Object> delete(@PathVariable String id, @RequestParam(required = false) String reason) {
        payVoucherService.delete(id, reason);
        invoiceService.deleteByRef(id, SERVICE_PAY);
        return R.success("删除成功");
    }

    @PostMapping("/preview")
    public R<List<UserTaskDTO>> preview(@Validated(ValidateGroup.Preview.class) @RequestBody PayVoucher payVoucher) {
        setSubmitUser(payVoucher);
        final List<UserTaskDTO> preview = payVoucherService.preview(payVoucher);
        return R.success(preview, preview.size());
    }

    @GetMapping("/record/{id}")
    public R<List<FlowOperationLog>> processRecord(@PathVariable String id) {
        final List<FlowOperationLog> processRecord = payVoucherService.processRecord(id, Constants.SERVICE_PAY);
        return R.success(processRecord, processRecord.size());
    }

    @GetMapping("/export")
    public void export(PayVoucherRequestForm requestForm, HttpServletResponse response) throws IOException {
        try {
            requestForm.setUserid(TokenUtil.getUseridFromAuthToken());
            payVoucherService.export(requestForm, response, excelTemplate,"pay_export.xlsx", PayVoucher.class);
        } catch (IOException e) {
            final R<Object> fail = R.fail("导出失败!");
            response.getWriter().println(JsonUtil.toJson(fail));
        }
    }

    @GetMapping("/export/dtl")
    public ResponseEntity<byte[]> exportDetail(String id, HttpServletResponse response) throws IOException {
        try {
            if (!Files.isRegularFile(Paths.get(detailTemplate))) {
                throw new BusinessException("未添加导出模板!");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", URLEncoder.encode("支付详情", StandardCharsets.UTF_8));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            final ReimburseExportDTO dto = payVoucherService.exportDetail(id);
            String newFileName =  id + ".xlsx";
            File newFile = new File(tempDir + "/pay/" + newFileName);
            try (ExcelWriter excelWriter = EasyExcel.write(newFile).withTemplate(detailTemplate).build()) {
                WriteSheet writeSheet = EasyExcel.writerSheet().build();
                excelWriter.fill(dto, writeSheet);
            }
            return new ResponseEntity<>(FileUtils.readFileToByteArray(newFile), headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage().getBytes(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    public void setSubmitUser(PayVoucher payVoucher) {
        UserView user = TokenUtil.getUserFromAuthToken();
        payVoucher.setSubmitUserid(user.getId());
        payVoucher.setSubmitUsername(user.getName());
    }

    public void setUser(PayVoucherRequestForm form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setUserid(user.getId());
        form.setUsername(user.getName());
    }







}
