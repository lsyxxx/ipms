package com.abt.wf.controller;

import com.abt.common.ExcelUtil;
import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.model.PurchaseApplyRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.PurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.abt.wf.config.Constants.*;

/**
 * 采购流程
 */
@RestController
@Slf4j
@RequestMapping("/purchase")
public class PurchaseController {

    private final PurchaseService purchaseService;


    /**
     *  临时下载保存地址
     */
    @Value("${abt.temp.dir}")
    private String tempDir;

    @Value("${abt.pur.tmp.name}")
    private String tempName;

    /**
     * 报销文件地址
     */
    @Value("${abt.rbs.pur.dir}")
    private String rbsPurDir;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }


    @PostMapping("/apply")
    public R<Object> apply(@RequestBody PurchaseApplyMain form) {
        setTokenUser(form);
        final PurchaseApplyMain entity = purchaseService.apply(form);
        purchaseService.setBusinessId(entity.getProcessInstanceId(), entity.getId());
        purchaseService.skipEmptyUserTask(entity);
        return R.success("提交采购申请成功");
    }

    @GetMapping("/preview")
    public R<List<UserTaskDTO>> preview(PurchaseApplyMain form) {
        if (form == null) {
            form = new PurchaseApplyMain();
        }
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(user.getId());
        form.setSubmitUsername(user.getUsername());
        final List<UserTaskDTO> preview = purchaseService.preview(form);
        return R.success(preview);
    }

    /**
     * 暂存草稿
     */
    @PostMapping("/temp")
    public R<Object> tempSave(@RequestBody PurchaseApplyMain form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(user.getId());
        form.setSubmitUsername(user.getUsername());
        purchaseService.tempSave(form);
        return R.success("暂存草稿成功");
    }

    @GetMapping("/todo")
    public R<List<PurchaseApplyMain>> todoList(@ModelAttribute PurchaseApplyRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<PurchaseApplyMain> page = purchaseService.findMyTodoByQueryPageable(requestForm);
        return R.success(page.getContent(), (int) page.getTotalElements());
    }

    @GetMapping("/todo/count")
    public R<Integer> todoCount(@ModelAttribute PurchaseApplyRequestForm requestForm) {
        setTokenUser(requestForm);
        final int count = purchaseService.countMyTodo(requestForm);
        return R.success(count, "查询成功");
    }

    @GetMapping("/done")
    public R<List<PurchaseApplyMain>> doneList(@ModelAttribute PurchaseApplyRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<PurchaseApplyMain> page = purchaseService.findMyDoneByQueryPageable(requestForm);
        return R.success(page.getContent(), (int) page.getTotalElements());
    }

    @GetMapping("/myapply")
    public R<List<PurchaseApplyMain>> myApplyList(@ModelAttribute PurchaseApplyRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<PurchaseApplyMain> page = purchaseService.findMyApplyByQueryPageable(requestForm);
        return R.success(page.getContent(), (int) page.getTotalElements());
    }

    @GetMapping("/all")
    public R<List<PurchaseApplyMain>> allList(@ModelAttribute PurchaseApplyRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<PurchaseApplyMain> page = purchaseService.findAllByQueryPageable(requestForm);
        return R.success(page.getContent(), (int) page.getTotalElements());
    }


    @GetMapping("/load")
    public R<PurchaseApplyMain> load(String id) {
        final PurchaseApplyMain entity = purchaseService.load(id);
        return R.success(entity);
    }

    @PostMapping("/approve")
    public R<Object> approve(@RequestBody PurchaseApplyMain form) {
        setTokenUser(form);
        purchaseService.setCostVariable(form);
        purchaseService.saveEntity(form);
        purchaseService.approve(form);
        purchaseService.skipEmptyUserTask(form);
        return R.success("审批成功");
    }

    @GetMapping("/record/{id}")
    public R<List<FlowOperationLog>> processRecord(@PathVariable String id) {
        if (StringUtils.isEmpty(id)) {
            return R.warn("未传入审批编号", List.of());
        }
        final List<FlowOperationLog> records = purchaseService.processRecord(id, SERVICE_PURCHASE);
        return R.success(records);
    }

    @GetMapping("/restart")
    public R<PurchaseApplyMain> restart(String id) {
        final PurchaseApplyMain copyEntity = purchaseService.getCopyEntity(id);
        return R.success(copyEntity);
    }

    @GetMapping("/revoke")
    public R<Object> revoke(String id) {
        UserView user = TokenUtil.getUserFromAuthToken();
        purchaseService.revoke(id, user.getId(), user.getName());
        return R.success("已撤销");
    }

    /**
     * 验收
     */
    @PostMapping("/accept/all")
    public R<Object> accept(@RequestBody PurchaseApplyMain form) {
        //判断是否能验收
        if (!STATE_DETAIL_PASS.equals(form.getBusinessState())) {
            return R.fail("流程审批未通过，不能验收!");
        }
        if (!form.getCreateUserid().equals(TokenUtil.getUseridFromAuthToken())) {
            return R.fail(String.format("只有申请人(%s)才可验收，当前用户:%s", form.getCreateUsername(), TokenUtil.getUserFromAuthToken().getName()));
        }
        purchaseService.accept(form);
        return R.success("已全部验收");
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(String id) throws Exception {
        try {
            final File pdf = purchaseService.createPdf(id, tempDir + tempName);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", URLEncoder.encode("采购", StandardCharsets.UTF_8));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(FileUtils.readFileToByteArray(pdf), headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>("导出Pdf文件失败".getBytes(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/create/pdf")
    public R<String> createPdf(String id) throws Exception {
        File pdf = purchaseService.createPdf(id, rbsPurDir + System.currentTimeMillis() + ".pdf");
        return R.success(pdf.getAbsolutePath(), "生成pdf成功!");
    }

    @Secured("JS_PUR_DEL")
    @GetMapping("/del")
    public R<Object> delete(String id) {
        purchaseService.delete(id);
        return R.success("删除成功!");
    }


    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel(String id) throws Exception {
        try {
            final File excel = purchaseService.createExcel(id, tempDir + System.currentTimeMillis() + ".xlsx");
            // 设置HTTP响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", URLEncoder.encode("采购申请单", StandardCharsets.UTF_8));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(FileUtils.readFileToByteArray(excel), headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("文件下载失败: ", e);
            return new ResponseEntity<>(e.getMessage().getBytes(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/create/accept/excel")
    public ResponseEntity<byte[]> createAcceptExcel(String id) throws IOException {
        try {
            final PurchaseApplyMain main = purchaseService.load(id);
            accessAccept(main);

            // 设置HTTP响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", URLEncoder.encode("采购验收单", StandardCharsets.UTF_8));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            final File excel = purchaseService.createAcceptExcel(main, tempDir + System.currentTimeMillis() + ".xlsx");
            return new ResponseEntity<>(FileUtils.readFileToByteArray(excel), headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("文件下载失败: ", e);
            return new ResponseEntity<>(e.getMessage().getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/export/accept/pdf")
    public ResponseEntity<byte[]> createAcceptPdf(String id) throws Exception {
        try {
            final PurchaseApplyMain main = purchaseService.load(id);
            accessAccept(main);
            // 设置HTTP响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", URLEncoder.encode("采购验收单", StandardCharsets.UTF_8));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            final File excel = purchaseService.createAcceptExcel(main, tempDir + System.currentTimeMillis() + ".xlsx");
            final File file = ExcelUtil.excel2Pdf(excel.getAbsolutePath(), tempDir + System.currentTimeMillis() + ".pdf");
            return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("文件下载失败: ", e);
            return new ResponseEntity<>(e.getMessage().getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public void accessAccept(PurchaseApplyMain main) {
        if (!main.isFinished()) {
            throw new BusinessException("采购申请流程未完成，无法生成验收单!");
        }
        if (!main.isAccepted()) {
            throw new BusinessException("采购申请单未验收，请申请人验收后再生成验收单");
        }
    }


    public void setTokenUser(PurchaseApplyRequestForm form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setUserid(user.getId());
        form.setUsername(user.getName());
    }

    public void setTokenUser(PurchaseApplyMain form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(user.getId());
        form.setSubmitUsername(user.getUsername());
    }

}
