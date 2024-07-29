package com.abt.oa.controller;

import com.abt.common.model.R;
import com.abt.common.model.User;
import com.abt.common.util.TokenUtil;
import com.abt.oa.entity.FieldWork;
import com.abt.oa.entity.FieldWorkAttendanceSetting;
import com.abt.oa.model.FieldWorkRequestForm;
import com.abt.oa.service.FieldWorkService;
import com.abt.sys.model.dto.UserView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    public FieldController(FieldWorkService fieldWorkService) {
        this.fieldWorkService = fieldWorkService;
    }

    /**
     * 删除一个野外补助选项
     */
    @GetMapping("/del")
    public R<Object> deleteFieldOption(String id) {
        return R.success("删除成功");
    }

    @GetMapping("/setting/all")
    public R<List<FieldWorkAttendanceSetting>> findAllSettings() {
        final List<FieldWorkAttendanceSetting> allSettings = fieldWorkService.findAllSettings();
        return R.success(allSettings);
    }

    @GetMapping("/setting/all/enabled")
    public R<List<FieldWorkAttendanceSetting>> findAllEnabledAllowance() {
        final List<FieldWorkAttendanceSetting> allSettings = fieldWorkService.findAllEnabledAllowance();
        return R.success(allSettings);
    }

    @PostMapping("/update")
    public R<Object> update(@RequestBody FieldWorkAttendanceSetting setting) {
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
     * 查询审批记录列表
     */
    @PostMapping("/find/list")
    public R<List<FieldWork>> findRecordBy(@RequestBody FieldWorkRequestForm form) {
        return null;
    }

    /**
     * 提交考勤记录
     */
    @PostMapping("/add")
    public R<Object> add(@RequestBody FieldWork work) {
        fieldWorkService.saveFieldWork(work);
        return R.success("提交成功");
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

}
