package com.abt.wf.controller;

import com.abt.common.model.R;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.EmployeeInfoRequestForm;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.service.EmployeeService;
import com.abt.wf.entity.UserSignature;
import com.abt.wf.model.EmployeeSignatureDTO;
import com.abt.wf.service.UserSignatureService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.quartz.Job;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.List;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/ussig")
public class UserSignatureController {

    private final UserSignatureService userSignatureService;
    private final EmployeeService employeeService;
    private final Job job;

    public UserSignatureController(UserSignatureService userSignatureService, EmployeeService employeeService, Job job) {
        this.userSignatureService = userSignatureService;
        this.employeeService = employeeService;
        this.job = job;
    }

    //    @Secured("U_SIG_VIEW")
    @GetMapping("/find/all")
    public R<List<UserSignature>> getAllSignatures() {
        final List<UserSignature> list = userSignatureService.getAllUserSignatures();
        return R.success(list);
    }


    /**
     * 上传签名文件
     * 1. 删除原有签名文件
     * 2. 保存新的文件及数据库
     * @param file 文件
     * @param jobNumber 工号
     */
    @PostMapping("/upload")
    public R<Object> upload(MultipartFile file, String jobNumber) throws IOException {
        if (file.isEmpty()) {
            throw new BusinessException("未上传任何文件!");
        }
        final EmployeeInfo emp = employeeService.findByJobNumber(jobNumber);
        if (emp == null) {
            throw new BusinessException(String.format("工号为%s的用户不存在!", jobNumber));
        }
        UserSignature us = userSignatureService.findByJobNumber(jobNumber);
        if (us != null) {
            String orgPath = userSignatureService.createFilePath(us.getFileName());
            try {
                FileUtils.delete(new File(orgPath));
            } catch (NoSuchFileException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            us = UserSignature.create();
            us.setJobNumber(jobNumber);
            us.setUserName(emp.getName());
            us.setUserId(emp.getUserid());
        }
        final String fileName = userSignatureService.saveFile(file, jobNumber, emp.getName(), emp.getCompany());
        us.setFileName(fileName);
        final UserSignature signature = userSignatureService.saveSignature(us, userSignatureService.createFilePath(fileName));
        return R.success(signature.getFileName(), "上传成功!");
    }

    @GetMapping("/find/empall")
    public R<List<EmployeeSignatureDTO>> findAllEmployeeSignatures(@ModelAttribute EmployeeInfoRequestForm requestForm) {
        final List<EmployeeSignatureDTO> list = employeeService.findWithSignature(requestForm);
        return R.success(list, list.size());
    }

//    @Secured("U_SIG_DOWNLOAD")
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam String fileName, @RequestParam String name) throws IOException {

        final String filePath = userSignatureService.createFilePath(fileName);

        // 设置HTTP响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", URLEncoder.encode(name, StandardCharsets.UTF_8));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        try {
            return new ResponseEntity<>(FileUtils.readFileToByteArray(FileUtils.getFile(filePath)), headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("文件下载失败: ", e);
            return new ResponseEntity<>(e.getMessage().getBytes(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/del")
    public R<Object> delete(String jobNumber, String fullPath) {
        final UserSignature us = userSignatureService.findByJobNumber(jobNumber);
        if (us == null) {
            return R.success("无需删除");
        }
        FileUtils.deleteQuietly(FileUtils.getFile(fullPath));
        userSignatureService.deleteUserSignatureByJobNumber(jobNumber);
        return R.success("删除成功");
    }


}
