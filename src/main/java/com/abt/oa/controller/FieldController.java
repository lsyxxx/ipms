package com.abt.oa.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.model.Table;
import com.abt.common.model.User;
import com.abt.common.util.TimeUtil;
import com.abt.common.util.TokenUtil;
import com.abt.common.util.ValidateUtil;
import com.abt.oa.AttendanceUtil;
import com.abt.oa.entity.FieldWork;
import com.abt.oa.entity.FieldWorkAttendanceSetting;
import com.abt.oa.model.FieldWorkBoard;
import com.abt.oa.model.FieldWorkRequestForm;
import com.abt.oa.service.FieldWorkService;
import com.abt.oa.service.SettingService;
import com.abt.sys.model.dto.UserView;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public FieldController(FieldWorkService fieldWorkService, SettingService settingService) {
        this.fieldWorkService = fieldWorkService;
        this.settingService = settingService;
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
            msg.append("2024-08-09的补贴项目(123)保存失败!\n");
            msg.append("2024-08-10的补贴项目(111)保存失败!\n");
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
        fieldWorkService.withdraw(id, TokenUtil.getUseridFromAuthToken());
        return R.success("撤销成功!");
    }


    @GetMapping("/stat")
    public R<Table> statisticTable(String start, String end, String reviewerId) {
        start = "2024-07-26";
        end = "2024-08-25";
        reviewerId = "621faa40-f45c-4da8-9a8f-65b0c5353f40";
        final Table table = fieldWorkService.createStatData(start, end, reviewerId);
        return R.success(table, "生成数据成功!");
    }



}
