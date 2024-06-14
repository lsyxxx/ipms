package com.abt.salary.controller;

import com.abt.common.model.R;
import com.abt.common.util.ValidateUtil;
import com.abt.salary.model.SalaryPreview;
import com.abt.salary.service.SalaryService;
import com.abt.sys.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
  * 
  */
@RestController
@Slf4j
@RequestMapping("/test/sl")
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
    public R<SalaryPreview> upload(MultipartFile file, String yearMonth, String group, Integer sheetNo, HttpServletRequest request) {
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
        clearSession();
        SalaryPreview preview = new SalaryPreview();

        //1. 保存文件信息
        //2. 抽取数据
        //3. 校验异常数据
        //4. 形成salaryPreview
        //5. 数据保存在session
        //6. response



//        final SystemFile sysfile = salaryService.saveSalaryExcel(file, yearMonth);
//        System.out.println("上传文件: " + sysfile.toString());
//        session.setAttribute(S_SL_FILE, sysfile);
//        final SalaryMain slm = salaryService.createSalaryMain(yearMonth, group, "", sysfile.getFullPath(), sysfile.getName());
//        session.setAttribute(S_SL_MAIN, slm);
//        final SalaryPreview salaryPreview = salaryService.previewSalaryDetail(file.getInputStream(), 1, slm);
//        session.setAttribute(S_SL_MAP, salaryPreview);

        return R.success(preview);
    }

    //TODO: 清空session中相关数据
    private void clearSession() {

    }
}
