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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    public UserSignatureController(UserSignatureService userSignatureService, EmployeeService employeeService) {
        this.userSignatureService = userSignatureService;
        this.employeeService = employeeService;
    }

    //    @Secured("U_SIG_VIEW")
    @GetMapping("/find/all")
    public R<List<UserSignature>> getAllSignatures() {
        final List<UserSignature> list = userSignatureService.getAllUserSignatures();
        return R.success(list);
    }


    /**
     * 上传签名文件
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
        String fullPath = userSignatureService.saveFile(file, jobNumber, emp.getName());
        userSignatureService.saveSignature(jobNumber, emp.getName(), fullPath);
        return R.success("上传成功!");
    }

    @GetMapping("/find/empall")
    public R<List<EmployeeSignatureDTO>> findAllEmployeeSignatures(@ModelAttribute EmployeeInfoRequestForm requestForm) {
        final List<EmployeeSignatureDTO> list = employeeService.findWithSignature(requestForm);
        return R.success(list, list.size());
    }


}
