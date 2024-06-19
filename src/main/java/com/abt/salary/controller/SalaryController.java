package com.abt.salary.controller;

import com.abt.common.model.R;
import com.abt.common.model.ValidationResult;
import com.abt.common.util.TokenUtil;
import com.abt.common.util.ValidateUtil;
import com.abt.salary.entity.SalaryCell;
import com.abt.salary.entity.SalaryMain;
import com.abt.salary.entity.SalarySlip;
import com.abt.salary.model.PwdForm;
import com.abt.salary.model.SalaryPreview;
import com.abt.salary.model.UserSlip;
import com.abt.salary.service.SalaryService;
import com.abt.sys.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * 查看上传工资表记录
     * @param yearMonth 发送年月
     */
    @GetMapping("/find/main/record")
    public R<List<SalaryMain>> findImportedSalaryRecordByYearMonth(@RequestParam(required = false) String yearMonth) {
        final List<SalaryMain> list = salaryService.findImportRecordBy(yearMonth);
        //TODO: 通知
        return R.success(list);
    }


    /**
     * 查看发送详情
     * 1. 发送的salarySlip，显示实发工资
     * @param mid 关联id
     */
    @GetMapping("/find/slips")
    public R<List<SalarySlip>> findSendSlip(String mid) {
        final List<SalarySlip> list = salaryService.findSendSlips(mid);
        return R.success(list);
    }

    /**
     * 发送工资条详情
     */
    @GetMapping("/find/cells")
    public R<List<SalaryCell>> findSalaryCellsBySlipId(String slipId, String mid) {
        final List<SalaryCell> list = salaryService.getSalaryDetail(slipId, mid);
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

    //------------------------------
    // 我的薪资
    //------------------------------
    /**
     * 进入页面的验证
     */
    @GetMapping("/my/entry")
    public R<Boolean> entry(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setAttribute(S_SL_MY, LocalDateTime.now());
        final String jobNumber = TokenUtil.getUserJobNumberFromAuthToken();
        //首次进入
        final boolean isFirst = salaryService.verifyFirst(jobNumber);
        return R.success(isFirst);
    }

    @GetMapping("/my/find/slip/u")
    public R<List<UserSlip>> findUserSalaryDetailsYear(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        salaryService.verifySessionTimeout(session);
        String jobNumber = TokenUtil.getUserJobNumberFromAuthToken();
        final List<UserSlip> list = salaryService.findUserSlipListByCurrentYear(jobNumber);
        return R.success(list);
    }

    /**
     * 重置密码为初始状态
     */
    @GetMapping("/pwd/reset")
    public R<Object> resetFirst() {
        salaryService.resetFirst();
        return R.success("已重置");
    }

    /**
     * 修改密码
     */
    @PostMapping("/my/pwd/update")
    public R<Object> updateSalaryPwd(@RequestBody PwdForm form) {
        String jobNumber = TokenUtil.getUserJobNumberFromAuthToken();
        salaryService.verifyPwd(form.getPwd1(), jobNumber);
//        if (!result.isPass()) {
//            return R.fail(result.getDescription());
//        }
        salaryService.updateEnc(form.getPwd2(), jobNumber);
        return R.success("修改成功");
    }

    /**
     * 初次修改密码
     */
    @PostMapping("/my/pwd/first")
    public R<Object> firstReset(@RequestBody PwdForm form) {
        final String jobNumber = TokenUtil.getUserJobNumberFromAuthToken();
        final boolean isFirst = salaryService.verifyFirst(jobNumber);
        if (!isFirst) {
            throw new BusinessException("请输入旧密码");
        }
        salaryService.updateEnc(form.getPwd1(), jobNumber);
        return R.success("密码已修改");
    }

    @PostMapping("/my/pwd/verify")
    public R<Object> verifyPwd(@RequestBody PwdForm form, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        salaryService.verifySessionTimeout(session);
        String jobNumber = TokenUtil.getUserJobNumberFromAuthToken();
        salaryService.verifyPwd(form.getPwd1(), jobNumber);
        return R.success("验证成功");
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
