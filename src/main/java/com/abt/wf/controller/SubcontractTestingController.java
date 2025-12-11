package com.abt.wf.controller;

import cn.idev.excel.FastExcel;
import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.market.model.ImportSample;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.SubcontractTesting;
import com.abt.wf.entity.SubcontractTestingSample;
import com.abt.wf.model.SbctSummaryData;
import com.abt.wf.model.SubcontractTestingRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.projection.SubcontractTestingSettlementDetailProjection;
import com.abt.wf.service.SubcontractTestingService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

//    /**
//     * 读取申请单的样品列表
//     * @param id 申请单Id
//     */
//    @GetMapping("/load/samples")
//    public R<List<SubcontractTestingSample>> loadSampleList(@RequestParam String id) {
//        if (StringUtils.isBlank(id)) {
//            throw new BusinessException("审批编号不能为空!");
//        }
//        subcontractTestingService.validateEntity(id);
//        final List<SubcontractTestingSample> samples = subcontractTestingService.getSamples(id);
//        return R.success(samples);
//    }


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

    @GetMapping("/export/samples")
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

    /**
     * 验证是否存在重复外送的样品
     * @param form 提交的表单
     */
    @PostMapping("/validate/duplicate")
    public R<Object> validateDuplicatedSample(@RequestBody SubcontractTesting form) {
        final List<SubcontractTestingSample> samples = subcontractTestingService.validateDuplicatedSample(form);
        if (samples != null && samples.size() > 0) {
            return R.fail(samples, "存在重复外送样品");
        }
        return R.success("无重复外送样品");
    }



    @PostMapping("/find/apply/samples")
    public R<Page<SubcontractTestingSample>> findApplySamples(@RequestBody SubcontractTestingRequestForm requestForm) {
        // 仅使用ids
        if (requestForm.getIds() == null || requestForm.getIds().isEmpty()) {
            return R.success(Page.empty(), "查询成功");
        }
        Map<String, SubcontractTesting> map = new HashMap<>();
        for (String id : requestForm.getIds()) {
            final SubcontractTesting main = subcontractTestingService.load(id);
            map.put(main.getId(), main);
        }
        PageRequest pageRequest = requestForm.createPageable(Sort.by(Sort.Order.asc("newSampleNo")));
        final Page<SubcontractTestingSample> samples = subcontractTestingService.findSamplesAndMarkDuplicated(requestForm.getIds(), pageRequest);
        for (SubcontractTestingSample sample : samples) {
            final SubcontractTesting main = map.get(sample.getMid());
            if (main != null) {
                sample.setEntrustCompany(main.getSubcontractCompanyName());
            }
        }
        return R.success(samples, "查询成功!");
    }

    @PostMapping("/find/apply/duplicated")
    public R<List<SubcontractTestingSample>> findDuplicatedSamples(@RequestBody SubcontractTestingRequestForm requestForm) {
        if (requestForm.getIds() == null || requestForm.getIds().isEmpty()) {
            throw new BusinessException("请传入外送申请单号!");
        }
        final List<SubcontractTestingSample> list = subcontractTestingService.findDuplicatedSamples(requestForm.getIds());
        return R.success(list, "查询重复外送样品成功!");
    }

    /**
     * 获取样品汇总
     */
    @PostMapping("/find/apply/smry")
    public R<List<SbctSummaryData>> getApplySummaryData(@RequestBody SubcontractTestingRequestForm requestForm) {
        if (requestForm.getIds() == null || requestForm.getIds().isEmpty()) {
            throw new BusinessException("请传入外送申请审批编号!");
        }
        final List<SbctSummaryData> summaryData = subcontractTestingService.getSummaryData(requestForm.getIds());
        return R.success(summaryData, "查询成功!");
    }

    /**
     * 导出表单
     */
    @GetMapping("/export/form")
    public void exportForm(String id, HttpServletResponse response) throws IOException {
        try {
            String fileName = "外送申请单.xlsx";
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"");
            subcontractTestingService.createFormExcel(id, response.getOutputStream());
        } catch (Exception e) {
            log.error("导出表单失败: ", e);
            throw new BusinessException("导出表单失败: " + e.getMessage());
        }   
    }


    /**
     * 导入样品excel
     */
    @GetMapping("/import/bysamples")
    public R<String> importSampleExcel(MultipartFile file, HttpServletResponse response) throws IOException {
        List<ImportSample> list = FastExcel.read(file.getInputStream())
                .head(ImportSample.class)
                .sheet()  // 默认读取第一个sheet
                .doReadSync();
        final String tempId = subcontractTestingService.importBySamples(list);

        return R.success(tempId, "导入成功");
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
