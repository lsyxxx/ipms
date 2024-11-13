package com.abt.oa.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.model.Table;
import com.abt.common.model.User;
import com.abt.common.util.TimeUtil;
import com.abt.common.util.TokenUtil;
import com.abt.common.util.ValidateUtil;
import com.abt.oa.AttendanceUtil;
import com.abt.oa.OAConstants;
import com.abt.oa.entity.FieldWork;
import com.abt.oa.entity.FieldWorkAttendanceSetting;
import com.abt.oa.model.FieldConfirmRequestForm;
import com.abt.oa.model.FieldConfirmResult;
import com.abt.oa.model.FieldWorkBoard;
import com.abt.oa.model.FieldWorkRequestForm;
import com.abt.oa.service.FieldWorkService;
import com.abt.oa.service.SettingService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.DataPrivilegeRule;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.service.EmployeeService;
import com.abt.sys.service.PermissionService;
import freemarker.template.TemplateException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

/**
 * 野外相关
 */
@RestController
@Slf4j
@RequestMapping("/field")
public class FieldController {
    private final FieldWorkService fieldWorkService;
    private final SettingService settingService;
    private final EmployeeService employeeService;
    private final PermissionService permissionService;

    public FieldController(FieldWorkService fieldWorkService, SettingService settingService, EmployeeService employeeService, PermissionService permissionService) {
        this.fieldWorkService = fieldWorkService;
        this.settingService = settingService;
        this.employeeService = employeeService;
        this.permissionService = permissionService;
    }

    @GetMapping("/setting/all")
    public R<List<FieldWorkAttendanceSetting>> findAllSettings() {
        final List<FieldWorkAttendanceSetting> allSettings = fieldWorkService.findLatestSettings();
        return R.success(allSettings);
    }

    @GetMapping("/setting/all/enabled")
    public R<List<FieldWorkAttendanceSetting>> findAllEnabledAllowance() {
        final List<FieldWorkAttendanceSetting> allSettings = fieldWorkService.findAllEnabledAllowance();
        return R.success(allSettings);
    }


    @GetMapping("/setting/his")
    public R<Object> findHistorySettingsByVid(String vid) {
        final List<FieldWorkAttendanceSetting> list = fieldWorkService.findHistorySettings(vid);
        return R.success(list);
    }

    @PostMapping("/update")
    public R<Object> update(@Validated(ValidateGroup.Save.class) @RequestBody FieldWorkAttendanceSetting setting) {
        fieldWorkService.saveSetting(setting);
        return R.success("更新成功!");
    }

    /**
     * 获取默认审批人
     */
    @GetMapping("/find/rvw")
    public R<User> getDefaultRzeviewer(String dept) {
        final User userReviewer = fieldWorkService.findUserReviewer(dept);
        return R.success(userReviewer);

    }

    /**
     * 查询审批记录
     * @param query 查询条件
     */
    @PostMapping("/find/record")
    public R<List<FieldWork>> findUserRecord(@RequestBody FieldWork query) {
        final List<FieldWork> userRecord = fieldWorkService.findUserRecord(query);
        return R.success(userRecord);
    }

    /**
     * 提交考勤记录
     */
    @PostMapping("/add")
    public R<Object> add(@Validated(value = {ValidateGroup.Save.class})  @RequestBody FieldWork work) {
        work.setConfirm(false);
        final Boolean isDup = fieldWorkService.isDuplicatedDate(work.getAttendanceDate(), work.getUserid());
        if (isDup) {
           return R.bizException(isDup, "重复提交");
        }
        fieldWorkService.saveFieldWork(work);
        return R.success("提交成功");
    }

    @PostMapping("/add/list")
    public R<Object> submitList(@RequestBody List<FieldWork> list) {
        final UserView user = TokenUtil.getUserFromAuthToken();
        final List<FieldWork> error = fieldWorkService.saveFieldWorkList(list, user.getId(), user.getName());
        if (error != null && !error.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            for (FieldWork e : error) {
                msg.append(String.format("%s的补贴项目(%s)保存失败!\n", TimeUtil.yyyy_MM_ddString(e.getAttendanceDate()), e.getSingleId()));
            }
            return R.warn(null, msg);
        }
        return R.success("保存成功!");
    }

    /**
     * 审批
     */
    @GetMapping("/rvw")
    public R<Object> review(String result, @RequestParam(required = false) String reason, String id) {
        final UserView user = TokenUtil.getUserFromAuthToken();
        FieldWork fw = new FieldWork();
        fw.setId(id);
        fw.setReviewResult(result);
        fw.setReviewReason(reason);
        fw.setReviewerId(user.getId());
        fw.setReviewerName(user.getName());
        fw.setReviewTime(LocalDateTime.now());
        fieldWorkService.saveFieldWork(fw);
        return R.success("审批成功");
    }

    @GetMapping("/find/todo")
    public R<List<FieldWork>> findTodoRecords(@ModelAttribute FieldWorkRequestForm form) {
        final UserView user = TokenUtil.getUserFromAuthToken();
        form.setUserid(user.getId());
        final Page<FieldWork> page = fieldWorkService.findTodoRecords(form);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/find/done")
    public R<List<FieldWork>> findDoneRecords(@ModelAttribute FieldWorkRequestForm form) {
        final UserView user = TokenUtil.getUserFromAuthToken();
        form.setUserid(user.getId());
        final Page<FieldWork> page = fieldWorkService.findDoneRecords(form);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/find/apply")
    public R<List<FieldWork>> findApplyRecords(@ModelAttribute FieldWorkRequestForm form) {
        final UserView user = TokenUtil.getUserFromAuthToken();
        form.setUserid(user.getId());
        final Page<FieldWork> page = fieldWorkService.findApplyRecords(form);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/find/all")
    public R<List<FieldWork>> findAllRecords(@ModelAttribute FieldWorkRequestForm form) {
        final UserView user = TokenUtil.getUserFromAuthToken();
        form.setUserid(user.getId());
        final Page<FieldWork> page = fieldWorkService.findAllRecords(form);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/find/atd")
    public R<List<FieldWork>> findAtdRecord(@ModelAttribute FieldWorkRequestForm form) {
        final UserView user = TokenUtil.getUserFromAuthToken();
        form.setUserid(user.getId());

        final Page<FieldWork> page = fieldWorkService.findAtdRecord(form);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }


    @GetMapping("/rvw/reject")
    public R<Object> reject(@RequestParam(required = false) String id, @RequestParam(required = false) String reason) {
        ValidateUtil.ensurePropertyNotnull(id, "考勤记录id");
        ValidateUtil.ensurePropertyNotnull(reason, "审批拒绝原因");
        fieldWorkService.reject(id, TokenUtil.getUseridFromAuthToken(), reason);
        return R.success("已拒绝");
    }

    @GetMapping("/rvw/pass")
    public R<Object> pass(@RequestParam(required = false) String ids) {
        ValidateUtil.ensurePropertyNotnull(ids, "至少选择一条考勤记录");
        String[] list = ids.split(",");
        int count = 0;
        for (String id : list) {
            fieldWorkService.pass(id, TokenUtil.getUseridFromAuthToken());
            count++;
        }

        return R.success("已审批" + count + "条考勤记录");
    }

    /**
     * 用户看板数据
     */
    @GetMapping("/board/user")
    public R<FieldWorkBoard> userBoard(@RequestParam(required = false) String jobNumber,
                                           @RequestParam(required = false) String userid,
                                           @RequestParam(required = false) String startDate,
                                           @RequestParam(required = false) String endDate) {
        if (StringUtils.isBlank(jobNumber)) {
            jobNumber = TokenUtil.getUserJobNumberFromAuthToken();
        }
        if (StringUtils.isBlank(userid)) {
            userid = TokenUtil.getUseridFromAuthToken();
        }
        LocalDate start = null, end = null;
        if (StringUtils.isBlank(startDate)) {
            //当前时间的考勤月起始
            String startDay = settingService.getAttendanceStartDay().getFvalue();
            start = AttendanceUtil.currentStartDate(Integer.parseInt(startDay));
        } else {
            start = TimeUtil.toLocalDate(startDate);
        }

        if (StringUtils.isBlank(endDate)) {
            //当前时间的考勤月结束
            String endDay = settingService.getAttendanceEndDay().getFvalue();
            end = AttendanceUtil.currentEndDate(Integer.parseInt(endDay));
        } else {
            end = TimeUtil.toLocalDate(endDate);
        }

        final FieldWorkBoard board = fieldWorkService.userBoard(jobNumber, userid, TimeUtil.yyyy_MM_ddString(start), TimeUtil.yyyy_MM_ddString(end));
        return R.success(board);
    }

    @GetMapping("/del")
    public R<Object> deleteFieldWork(String id) {
        final UserView user = TokenUtil.getUserFromAuthToken();
        fieldWorkService.deleteFieldWork(id, TokenUtil.getUseridFromAuthToken());
        return R.success("删除成功!");
    }


    @GetMapping("/withdraw")
    public R<Object> withdrawRecord(String id) {
        //TODO: 确认过考勤不可修改
        fieldWorkService.withdraw(id, TokenUtil.getUseridFromAuthToken());
        return R.success("撤销成功!");
    }

    public static final String MGR_BOARD_SOURCE_CODE = "mgrBoard";

    public static final String SESSION_FW_MGR_TABLE = "session_fw_mgr_table";

    /**
     * 查看统计数据
     */
    @PostMapping("/stat")
    public R<Table> statisticTable(HttpServletRequest request,
                                   @RequestBody FieldWorkRequestForm form) {
        String yearMonth = form.getYearMonth();
        if (StringUtils.isBlank(yearMonth)) {
            return R.fail("请选择考勤年月");
        }
        YearMonth ym = TimeUtil.toYearMonth(form.getYearMonth());
        final int year = ym.getYear();
        final int monthValue = ym.getMonthValue();
        String dept = form.getDept();
        String company = form.getCompany();
        String startDay = settingService.getAttendanceStartDay().getFvalue();
        String endDay = settingService.getAttendanceEndDay().getFvalue();
        final LocalDate start = LocalDate.of(year, monthValue, Integer.parseInt(startDay)).minusMonths(1);
        final LocalDate end = LocalDate.of(year, monthValue, Integer.parseInt(endDay));

        UserView user = TokenUtil.getUserFromAuthToken();
        final EmployeeInfo emp = employeeService.findByJobNumber(user.getEmpnum());

        //判断用户数据权限
        //1. 如果系统中权限配置了，那么可以看到所有
        //2. 当前登录用户没有配置权限，那么当前登录用户只能查看本部门的考勤
        final DataPrivilegeRule rules = permissionService.getDataPrivilegeRuleBySourceCode(MGR_BOARD_SOURCE_CODE);
        final boolean hasAuth = rules.checkRule(user);
        if (!hasAuth) {
            //查看本部门
            if (StringUtils.isBlank(dept)) {
                dept = emp.getDept();
            }
        }
        List<FieldWork> records = fieldWorkService.findAtdByUserInfo(null, dept, company, start, end);
        final Table table = fieldWorkService.createStatData(yearMonth, start, end, records);
        table.setCompany(company);
        //申请session
        HttpSession session = request.getSession();
        if (session == null) {
            session = request.getSession(true);
        }
        session.removeAttribute(SESSION_FW_MGR_TABLE);
        request.getSession().setAttribute(SESSION_FW_MGR_TABLE, table);
        return R.success(table, "生成数据成功!");
    }

    @GetMapping("/stat/export")
    public R<String> exportExcel(HttpServletRequest request) throws TemplateException, IOException {
        HttpSession session = request.getSession();
        if (session == null) {
            throw new BusinessException("Session超时，请重新生成数据");
        }
        Object attribute = session.getAttribute(SESSION_FW_MGR_TABLE);
        if (attribute == null) {
            throw new BusinessException("请先生成考勤数据再导出");
        }
        Table table = (Table) attribute;
        File file = fieldWorkService.writeExcel(table);

        return R.success(file.getAbsolutePath(), "生成excel数据成功!");
    }

    @GetMapping("/dtl")
    public R<FieldWork> detail(String id) {
        final FieldWork detail = fieldWorkService.detail(id);
        return R.success(detail);
    }

    /**
     * 确认月考勤，以月为单位
     */
    @PostMapping("/confirm/do")
    public R<String> confirmRecords(@RequestBody FieldConfirmRequestForm form) {
        if (StringUtils.isBlank(form.getUserid())) {
            form.setUserid(TokenUtil.getUseridFromAuthToken());
        }
        final int confirm = fieldWorkService.confirm(form.getUserid(), form.getIds());
        return R.success(String.format("已确认%d条考勤数据", confirm));
    }

    @PostMapping("/confirm/stat")
    public R<FieldConfirmResult> getConfirmRecords(@RequestBody FieldWorkRequestForm form) {
        if (StringUtils.isNotBlank(form.getYearMonth())) {
            //选择考勤月的起止日期
            String startDay = settingService.getAttendanceStartDay().getFvalue();
            String endDay = settingService.getAttendanceEndDay().getFvalue();
            final YearMonth yearMonth = TimeUtil.toYearMonth(form.getYearMonth());
            final YearMonth startMonth = yearMonth.minusMonths(1);
            LocalDate startDate = LocalDate.of(startMonth.getYear(), startMonth.getMonth(), Integer.parseInt(startDay));
            LocalDate endDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), Integer.parseInt(endDay));
            form.setStartDate(TimeUtil.yyyy_MM_ddString(startDate));
            form.setEndDate(TimeUtil.yyyy_MM_ddString(endDate));
        }
        if (StringUtils.isBlank(form.getUserid())) {
            form.setUserid(TokenUtil.getUseridFromAuthToken());
        }
        form.setLimit(9999);
        form.setState(OAConstants.FW_PASS);
        final FieldConfirmResult stat = fieldWorkService.getConfirmStat(form);
        return R.success(stat);
    }

    /**
     * 同一天重复提交
     * @param date 考勤日期
     * @param userid 考勤用户id
     */
    @GetMapping("/verify/date")
    public R<Boolean> verifyDuplicatedDate(LocalDate date, String userid) {
        final Boolean duplicatedDate = fieldWorkService.isDuplicatedDate(date, userid);
        return R.success(duplicatedDate);
    }



}
