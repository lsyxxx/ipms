package com.abt.wf.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.util.JsonUtil;
import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseExportDTO;
import com.abt.wf.model.ReimburseRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.ReimburseService;
import cn.idev.excel.EasyExcel;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.write.metadata.WriteSheet;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/wf/rbs")
public class ReimburseController {

    private final ReimburseService reimburseService;
    @Value("${abt.rbs.excel.template}")
    private String excelTemplate;

    @Value("${abt.rbs.excel.dtl.template}")
    private String detailTemplate;

    /**
     *  临时下载保存地址
     */
    @Value("${abt.temp.dir}")
    private String tempDir;

    public ReimburseController(ReimburseService reimburseService) {
        this.reimburseService = reimburseService;
    }

    /**
     * 撤销一个流程
     * @param id: 撤销的流程的id
     */
    @GetMapping("/revoke")
    public R<Object> revoke(String id) {
        UserView user = TokenUtil.getUserFromAuthToken();
        reimburseService.revoke(id, user.getId(), user.getName());
        return R.success("撤销成功");
    }

    @GetMapping("/restart")
    public R<Reimburse> copyEntity(String id) throws Exception {
        final Reimburse copyEntity = reimburseService.getCopyEntity(id);
        return R.success(copyEntity);
    }

    @PostMapping("/apply")
    public R<Object> apply(@Validated({ValidateGroup.Apply.class}) @RequestBody Reimburse form) {
        setSubmitUser(form);
        form.setRbsDate(LocalDate.now());
        reimburseService.apply(form);
        return R.success();
    }

    @PostMapping("/approve")
    public R<Object> approve(@RequestBody Reimburse form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(user.getId());
        form.setSubmitUsername(user.getUsername());
        reimburseService.approve(form);
        return R.success("审批成功");
    }

    @GetMapping("/todo")
    public R<List<Reimburse>> todoList(@ModelAttribute ReimburseRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<Reimburse> page = reimburseService.findMyTodoByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/done")
    public R<List<Reimburse>> doneList(ReimburseRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<Reimburse> page = reimburseService.findMyDoneByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());

    }

    @GetMapping("/myapply")
    public R<List<Reimburse>> myApplyList(ReimburseRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<Reimburse> page = reimburseService.findMyApplyByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/all")
    public R<List<Reimburse>> all(@ModelAttribute ReimburseRequestForm requestForm) {
        final Page<Reimburse> all = reimburseService.findAllByQueryPageable(requestForm);
        return R.success(all.getContent(), (int)all.getTotalElements());
    }

    @GetMapping("/load/{id}")
    public R<Reimburse> load(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException("审批编号不能为空!");
        }
        Reimburse rbs = reimburseService.load(id);
        return R.success(rbs);
    }


    //删除权限：财务流程-删除
    @Secured("849b61e0-6f97-454c-b729-f3ebc821953e")
    @GetMapping("/del/{id}")
    public R<Object> delete(@PathVariable String id, String reason) {
        reimburseService.delete(id, reason);
        return R.success("删除成功");
    }


    @PostMapping("/preview")
    public R<List<UserTaskDTO>> preview(@RequestBody Reimburse form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(user.getId());
        form.setSubmitUsername(user.getUsername());
        final List<UserTaskDTO> preview = reimburseService.preview(form);
        return R.success(preview, preview.size());
    }


    @GetMapping("/record/{id}")
    public R<List<FlowOperationLog>> processRecord(@PathVariable String id) {
        final List<FlowOperationLog> processRecord = reimburseService.processRecord(id, Constants.SERVICE_PAY);
        return R.success(processRecord, processRecord.size());
    }

    @GetMapping("/export")
    public void export(ReimburseRequestForm requestForm, HttpServletResponse response) throws IOException {
        try {
            requestForm.setUserid(TokenUtil.getUseridFromAuthToken());
            reimburseService.export(requestForm, response, excelTemplate,"rbs_export.xlsx", Reimburse.class);
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
            headers.setContentDispositionFormData("attachment", URLEncoder.encode("报销详情", StandardCharsets.UTF_8));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            final ReimburseExportDTO dto = reimburseService.exportDetail(id);
            String newFileName =  id + ".xlsx";
            File newFile = new File(tempDir + "/rbs/" + newFileName);
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

    public void setTokenUser(ReimburseRequestForm form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setUserid(user.getId());
        form.setUsername(user.getName());
    }

    public void setSubmitUser(Reimburse form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(user.getId());
        form.setSubmitUsername(user.getUsername());
    }
}
