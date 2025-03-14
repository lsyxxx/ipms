package com.abt.salary.controller;

import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.common.util.ValidateUtil;
import com.abt.oa.entity.OrgLeader;
import com.abt.oa.service.OrgLeaderService;
import com.abt.salary.AutoCheckSalaryJob;
import com.abt.salary.entity.SalaryMain;
import com.abt.salary.entity.SalarySlip;
import com.abt.salary.model.*;
import com.abt.salary.service.SalaryService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.model.entity.Role;
import com.abt.sys.service.CompanyService;
import com.abt.sys.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.abt.salary.Constants.*;
import static com.abt.salary.model.CheckAuth.*;

/**
  * 
  */
@RestController
@Slf4j
@RequestMapping("/sl")
public class SalaryController {

    private final SalaryService salaryService;
    private final EmployeeService employeeService;
    private final AutoCheckSalaryJob autoCheckSalaryJob;
    private final OrgLeaderService orgLeaderService;
    private final CompanyService companyService;


    public SalaryController(SalaryService salaryService, EmployeeService employeeService, AutoCheckSalaryJob autoCheckSalaryJob, OrgLeaderService orgLeaderService, CompanyService companyService) {
        this.salaryService = salaryService;
        this.employeeService = employeeService;
        this.autoCheckSalaryJob = autoCheckSalaryJob;
        this.orgLeaderService = orgLeaderService;
        this.companyService = companyService;
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
    public R<List<UserSalaryDetail>> findSalaryCellsBySlipId(String slipId, String mid) {
        final List<UserSalaryDetail> list = salaryService.getSalaryDetail(slipId, mid);
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
//        HttpSession session = request.getSession(false);
//        salaryService.verifySessionTimeout(session);
        String jobNumber = TokenUtil.getUserJobNumberFromAuthToken();
        final List<UserSlip> list = salaryService.findUserSlipListByCurrentYear(jobNumber);
        return R.success(list);
    }

    /**
     * 重置密码为初始状态
     */
    @Secured("JS_SL_ENC_RESET")
    @GetMapping("/enc/resetFirst")
    public R<Object> resetFirst(String jobNumber, String name) {
        final EmployeeInfo emp = employeeService.findByJobNumber(jobNumber);
        if (emp == null) {
            throw new BusinessException("未查询到员工(工号: " + jobNumber + ")");
        }
        if (!emp.getName().equals(name)) {
            throw new BusinessException("工号与姓名不一致");
        }
        salaryService.resetFirst(jobNumber);
        return R.success("已重置，请重新设置密码");
    }

    /**
     * 修改密码
     */
    @PostMapping("/my/pwd/update")
    public R<Object> updateSalaryPwd(@RequestBody PwdForm form) {
        String jobNumber = TokenUtil.getUserJobNumberFromAuthToken();
        ValidateUtil.ensurePropertyNotnull(form.getPwd1(), "请输入新密码");
        ValidateUtil.ensurePropertyNotnull(form.getPwd2(), "请输入新密码确认密码");
        ValidateUtil.ensurePropertyNotnull(form.getOldPwd(), "请输入旧密码");
        salaryService.updateEncNotFirst(form, jobNumber);
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
//        HttpSession session = request.getSession(false);
//        salaryService.verifySessionTimeout(session);
        String jobNumber = TokenUtil.getUserJobNumberFromAuthToken();
        salaryService.verifyPwd(form.getPwd1(), jobNumber);
        return R.success("验证成功");
    }

    @GetMapping("/my/find/slip/u/y")
    public R<List<UserSlip>> findUserSlipListByYearMonth(String yearMonth) {
        final List<UserSlip> list = salaryService.findUserSalarySlipByYearMonth(TokenUtil.getUserJobNumberFromAuthToken(), yearMonth);
        return R.success(list);
    }

    @GetMapping("/my/read")
    public R<Object> readSalarySlip(String id) {
        salaryService.readSalarySlip(id);
        return R.success("已读");
    }

    @GetMapping("/my/check")
    public R<Object> checkSalarySlip(String id) {
        salaryService.checkSalarySlip(id, SalarySlip.CHECK_TYPE_MANUAL);
        return R.success("已确认");
    }

    @GetMapping("/autocheck")
    public R<Object> autoCheck(LocalDateTime autoCheckTime) throws SchedulerException, ClassNotFoundException {
        autoCheckSalaryJob.createJobAndScheduler(autoCheckTime);
        return R.success("创建任务成功!");
    }

    /**
     * 查看部门的工资表
     */
    @GetMapping("/chk/dept/view")
    public R<SalaryPreview> findDeptSlipsBy(String mid) {
        //1. 用户是否有权限查看
        UserView uv = TokenUtil.getUserFromAuthToken();
        //1.3是否允许部门经理查看
        final SalaryMain main = salaryService.findSalaryMainById(mid);
        final String groupName = salaryService.translateCompanyName(main.getGroup());
        main.setGroupName(groupName);
        final CheckAuth auth = getCheckAuth(uv);
        if (SL_CHK_DM.equals(auth.getRole()) && !main.isDeptManagerCheck()) {
            //不允许部门经理查看
            auth.setRole(SL_CHK_USER);
            auth.clearDept();
        }
        //都没有，只能看个人的
        //2. 查询
        final SalaryPreview prev = salaryService.getSalaryCheckTable(auth, main);
        return R.success(prev);
    }

    @PostMapping("/chk/dept/do")
    public R<List<SalarySlip>> deptCheck(@RequestBody List<String> slipIds) {
        final CheckAuth auth = getCheckAuth(TokenUtil.getUserFromAuthToken());
        if (!SL_CHK_DCEO.equals(auth.getRole()) && !SL_CHK_DM.equals(auth.getRole())) {
            return R.fail("您无权审批(角色:" + auth.getRole() + ")");
        }
        return salaryService.deptCheck(auth, slipIds);
    }

    /**
     * 审批记录
     * @param yearMonth 工资年月
     */
    @GetMapping("/chk/list")
    public R<List<SalaryMain>> findCheckList(String yearMonth) {
        final CheckAuth checkAuth = getCheckAuth(TokenUtil.getUserFromAuthToken());
        if (SL_CHK_DM.equals(checkAuth.getRole()) || SL_CHK_DCEO.equals(checkAuth.getRole())) {
            final List<SalaryMain> slm = salaryService.findCheckList(yearMonth, checkAuth);
            return R.success(slm);
        }
        return R.success(List.of());
    }


    private void copyForm(SalaryMain slipForm, SalaryMain main) {
        main.setTitle(slipForm.getTitle());
        main.setShowTip(slipForm.isShowTip());
        main.setTip(slipForm.getTip());
    }

    private void clearSession(HttpSession session) {
        session.removeAttribute(S_SL_PREVIEW);
    }

    /**
     * 获取用户工资审批权限
     * @param uv UserView
     * @return CheckAuth 审批权限对象
     */
    public CheckAuth getCheckAuth(UserView uv) {
        CheckAuth auth = new CheckAuth();
        auth.setName(uv.getName());
        auth.setJobNumber(uv.getEmpnum());
        //1. 部门经理
        final EmployeeInfo emp = employeeService.findUserByUserid(uv.getId());
        if ("部门经理".equals(emp.getPosition())) {
            //是否是部门经理
            auth.setRole(SL_CHK_DM);
            auth.addDeptId(emp.getDept());
        }
        //1.2副总权限
        final List<OrgLeader> orgLeaders = orgLeaderService.findByJobNumber(uv.getEmpnum());
        if (!orgLeaders.isEmpty()) {
            //有副总权限，查看负责部门的
            auth.setRole(SL_CHK_DCEO);
            auth.clearAndSetAll(orgLeaders.stream().map(OrgLeader::getDeptId).toList());
        }
        //1.1 查看所有，具有查看所有的角色权限
        final Optional<Role> op = uv.getAuthorities().stream().filter(a -> a.getId().equals("SL_CHECK_ALL")).findFirst();
        if (op.isPresent()) {
            auth.setRole(SL_CHK_ALL);
            auth.clearDept();
        }

        return auth;
    }

}
