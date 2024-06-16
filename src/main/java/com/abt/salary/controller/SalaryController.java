package com.abt.salary.controller;

import com.abt.common.model.R;
import com.abt.common.util.ValidateUtil;
import com.abt.salary.entity.SalaryMain;
import com.abt.salary.model.SalaryPreview;
import com.abt.salary.service.SalaryService;
import com.abt.sys.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.abt.salary.Constants.*;

/**
  * 
  */
@RestController
@Slf4j
@RequestMapping("/sl")
public class SalaryController {

    private final SalaryService salaryService;


    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }


    /**
     * 上传工资excel
     * 1. 上传文件
     * 2. 解析文件
     */
    @PostMapping("/upload")
    @ResponseBody
    public R<SalaryPreview> upload(MultipartFile file, String yearMonth, String group, Integer sheetNo, HttpServletRequest request) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请上传工资表!");
        }
        if (sheetNo == null) {
            sheetNo = 1;
        }

        ValidateUtil.ensurePropertyNotnull(yearMonth, "必须选择发放工资年月(yearMonth)");
        ValidateUtil.ensurePropertyNotnull(group, "必须选择工资组(group)");

        //申请session
        HttpSession session = request.getSession();
        if (session == null) {
            session = request.getSession(true);
        }
        clearSession(session);
        final SalaryMain salaryMain = salaryService.createSalaryMain(file, yearMonth, group);
        final SalaryPreview salaryPreview = salaryService.extractAndValidate(file.getInputStream(), salaryMain.getId());
        //保存session
        session.setAttribute(S_SL_UUID, UUID.randomUUID().toString());
        session.setAttribute(S_SL_MAIN, salaryMain);
        session.setAttribute(S_SL_PREVIEW, salaryPreview);
        session.setAttribute(S_SL_FILE, file);

        return R.success(salaryPreview);
    }

    @GetMapping("/import")
    public void importDb(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session == null) {
            throw new BusinessException("会话失效，请刷新后重新进入");
        }
    }

    private void clearSession(HttpSession session) {
        session.removeAttribute(S_SL_MAIN);
        session.removeAttribute(S_SL_PREVIEW);
        session.removeAttribute(S_SL_FILE);
    }

}
