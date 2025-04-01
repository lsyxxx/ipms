package com.abt.wf.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.SubcontractTesting;
import com.abt.wf.model.SubcontractTestingRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.SubcontractTestingService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
        SubcontractTesting form = subcontractTestingService.load(id);
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

    @GetMapping("/export")
    public void export(SubcontractTestingRequestForm requestForm, HttpServletResponse response) throws IOException {
//        try {
//            requestForm.setUserid(TokenUtil.getUseridFromAuthToken());
//            subcontractTestingService.export(requestForm, response, excelTemplate,"rbs_export.xlsx", SubcontractTesting.class);
//        } catch (IOException e) {
//            final R<Object> fail = R.fail("导出失败!");
//            response.getWriter().println(JsonUtil.toJson(fail));
//        }
    }

    @GetMapping("/export/dtl")
    public ResponseEntity<byte[]> exportDetail(String id, HttpServletResponse response) throws IOException {
//        try {
//            if (!Files.isRegularFile(Paths.get(detailTemplate))) {
//                throw new BusinessException("未添加导出模板!");
//            }
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentDispositionFormData("attachment", URLEncoder.encode("报销详情", StandardCharsets.UTF_8));
//            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//            final SubcontractTestingExportDTO dto = subcontractTestingService.exportDetail(id);
//            String newFileName =  id + ".xlsx";
//            File newFile = new File(tempDir + "/rbs/" + newFileName);
//            try (ExcelWriter excelWriter = EasyExcel.write(newFile).withTemplate(detailTemplate).build()) {
//                WriteSheet writeSheet = EasyExcel.writerSheet().build();
//                excelWriter.fill(dto, writeSheet);
//            }
//            return new ResponseEntity<>(FileUtils.readFileToByteArray(newFile), headers, HttpStatus.OK);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            return new ResponseEntity<>(e.getMessage().getBytes(), HttpStatus.EXPECTATION_FAILED);
//        }
        return null;
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
