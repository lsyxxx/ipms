package com.abt.salary.controller;

import com.abt.common.model.R;
import com.abt.common.util.FileUtil;
import com.abt.common.util.ValidateUtil;
import com.abt.salary.entity.SalaryCell;
import com.abt.salary.entity.SalaryMain;
import com.abt.salary.entity.SalarySlip;
import com.abt.salary.model.SalaryPreview;
import com.abt.salary.service.SalaryService;
import com.abt.sys.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
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
            sheetNo = 0;
        }

        ValidateUtil.ensurePropertyNotnull(yearMonth, "必须选择发放工资年月(yearMonth)");
        ValidateUtil.ensurePropertyNotnull(group, "必须选择工资组(group)");

        //申请session
        HttpSession session = request.getSession();
        if (session == null) {
            session = request.getSession(true);
        }
        clearSession(session);
        final SalaryMain salaryMain = salaryService.createSalaryMain(file, yearMonth, group, sheetNo);
        final SalaryPreview salaryPreview = salaryService.extractAndValidate(file.getInputStream(), salaryMain);
        salaryService.saveExcelFile(file, salaryPreview.getSalaryMain());
        session.setAttribute(S_SL_PREVIEW, salaryPreview);

        return R.success(salaryPreview);
    }

    @GetMapping("/import")
    public R<Object> importDb(@ModelAttribute SalaryMain slipForm, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session == null) {
            throw new BusinessException("会话失效，请刷新后重新进入");
        }
        //校验
        SalaryPreview salaryPreview = (SalaryPreview) session.getAttribute(S_SL_PREVIEW);
        if (salaryPreview == null) {
            throw new BusinessException("数据超时，请刷新页面重新上传Excel");
        }
        SalaryMain main = salaryPreview.getSalaryMain();
        copyForm(slipForm, main);
        salaryService.salaryImport(salaryPreview.getSalaryMain(), salaryPreview);
        return R.success("发送成功!");
    }

    @GetMapping("/find/main/record")
    public R<List<SalaryMain>> findImportedSalaryRecordByYearMonth(@RequestParam(required = false) String yearMonth, @RequestParam(required = false) String group) {
        final List<SalaryMain> list = salaryService.findImportRecordBy(yearMonth, group);
        return R.success(list);
    }

    /**
     * 查看发送详情
     * @param mid 关联id
     */
    @GetMapping("/find/slips")
    public R<List<SalarySlip>> findSendSlip(String mid) {
        final List<SalarySlip> list = salaryService.findSendSlips(mid);
        return R.success(list);
    }

    /**
     * 删除一条上传记录，包含所有关联表数据
     * @param mid salaryMain.id
     */
    @GetMapping("/del")
    public R<Object> deleteUploadSalary(String mid) {
        salaryService.deleteSalaryRecord(mid);
        return R.success("删除成功");
    }

    @GetMapping("/groups")
    public R<List<String>> getSalaryGroup() {
        final List<String> list = salaryService.getSalaryGroup();
        return R.success(list);
    }

    private void copyForm(SalaryMain slipForm, SalaryMain main) {
        main.setTitle(slipForm.getTitle());
        main.setShowTip(slipForm.isShowTip());
        main.setTip(slipForm.getTip());
    }

    private void clearSession(HttpSession session) {
        session.removeAttribute(S_SL_PREVIEW);
    }

}
