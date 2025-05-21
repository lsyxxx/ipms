package com.abt.wf.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.SubcontractTesting;
import com.abt.wf.entity.SubcontractTestingSettlementDetail;
import com.abt.wf.model.SubcontractTestingRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.projection.SubcontractTestingSettlementDetailProjection;
import com.abt.wf.service.SubcontractTestingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

/**
 * 外送检测
 */
@RestController
@Slf4j
@RequestMapping("/wf/sbct")
public class SubcontractTestingController {

    private final SubcontractTestingService subcontractTestingService;

    public SubcontractTestingController(SubcontractTestingService subcontractTestingService) {
        this.subcontractTestingService = subcontractTestingService;
    }

    /**
     * 撤销一个流程
     * @param id: 撤销的流程的id
     */
    @GetMapping("/revoke")
    public R<Object> revoke(String id) {
        UserView user = TokenUtil.getUserFromAuthToken();
        subcontractTestingService.revoke(id, user.getId(), user.getName());
        return R.success("撤销成功");
    }

    @GetMapping("/restart")
    public R<SubcontractTesting> copyEntity(String id) {
        final SubcontractTesting copyEntity = subcontractTestingService.getCopyEntity(id);
        return R.success(copyEntity);
    }

    @PostMapping("/apply")
    public R<Object> apply(@Validated({ValidateGroup.Apply.class}) @RequestBody SubcontractTesting form) {
        setSubmitUser(form);
        final SubcontractTesting entity = subcontractTestingService.apply(form);
        subcontractTestingService.skipEmptyUserTask(entity);
        return R.success(entity, "提交成功");
    }

    @PostMapping("/approve")
    public R<Object> approve(@RequestBody SubcontractTesting form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(user.getId());
        form.setSubmitUsername(user.getUsername());
        subcontractTestingService.approve(form);
        subcontractTestingService.skipEmptyUserTask(form);
        return R.success("审批成功");
    }

    @GetMapping("/todo")
    public R<List<SubcontractTesting>> todoList(@ModelAttribute SubcontractTestingRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<SubcontractTesting> page = subcontractTestingService.findMyTodoByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/done")
    public R<List<SubcontractTesting>> doneList(SubcontractTestingRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<SubcontractTesting> page = subcontractTestingService.findMyDoneByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());

    }

    @GetMapping("/myapply")
    public R<List<SubcontractTesting>> myApplyList(SubcontractTestingRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<SubcontractTesting> page = subcontractTestingService.findMyApplyByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/all")
    public R<List<SubcontractTesting>> all(@ModelAttribute SubcontractTestingRequestForm requestForm) {
        final Page<SubcontractTesting> all = subcontractTestingService.findAllByQueryPageable(requestForm);
        return R.success(all.getContent(), (int)all.getTotalElements());
    }

    @GetMapping("/load/{id}")
    public R<SubcontractTesting> load(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException("审批编号不能为空!");
        }
        SubcontractTesting form = subcontractTestingService.loadWithSampleList(id);
        setSubmitUser(form);
        try {
            final boolean approveUser = subcontractTestingService.isApproveUser(form);
            form.setApproveUser(approveUser);
        } catch (BusinessException e) {
            form.setApproveUser(false);
        }
        return R.success(form);
    }


    //删除权限：财务流程-删除
    @GetMapping("/del/{id}")
    public R<Object> delete(@PathVariable String id, String reason) {
        subcontractTestingService.delete(id, reason);
        return R.success("删除成功");
    }


    @PostMapping("/preview")
    public R<List<UserTaskDTO>> preview(@RequestBody SubcontractTesting form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(user.getId());
        form.setSubmitUsername(user.getUsername());
        final List<UserTaskDTO> preview = subcontractTestingService.preview(form);
        return R.success(preview, preview.size());
    }


    @GetMapping("/record/{id}")
    public R<List<FlowOperationLog>> processRecord(@PathVariable String id) {
        final List<FlowOperationLog> processRecord = subcontractTestingService.processRecord(id, Constants.SERVICE_PAY);
        return R.success(processRecord, processRecord.size());
    }

    @GetMapping("/todo/count")
    public R<Integer> countTodo(@ModelAttribute SubcontractTestingRequestForm requestForm) {
        setTokenUser(requestForm);
        final int count = subcontractTestingService.countMyTodo(requestForm);
        return R.success(count, "查询成功");
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(String id) throws IOException {
        try {
            final String filePath = subcontractTestingService.exportSampleList(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", URLEncoder.encode("外送样品清单", StandardCharsets.UTF_8));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(FileUtils.readFileToByteArray(Paths.get(filePath).toFile()), headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("文件下载失败: ", e);
            return new ResponseEntity<>(e.getMessage().getBytes(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/find/companies")
    public R<List<String>> findAllSubcontractCompanies() {
        final List<String> list = subcontractTestingService.findAllApplySubcontractCompany();
        return R.success(list);
    }

    @GetMapping("/find/apply/dtl")
    public R<List<SubcontractTestingSettlementDetailProjection>> findApplyDetails(@ModelAttribute SubcontractTestingRequestForm requestForm) {
        requestForm.setUserid(TokenUtil.getUseridFromAuthToken());
        final List<SubcontractTestingSettlementDetailProjection> list = subcontractTestingService.findDetails(requestForm);
        return R.success(list);
    }

    public void setTokenUser(SubcontractTestingRequestForm form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setUserid(user.getId());
        form.setUsername(user.getName());
    }

    public void setSubmitUser(SubcontractTesting form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(user.getId());
        form.setSubmitUsername(user.getUsername());
    }
}
