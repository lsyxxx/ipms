package com.abt.oa.controller;

import com.abt.common.model.R;
import com.abt.oa.entity.FieldWorkAttendanceSetting;
import com.abt.oa.service.FieldWorkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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



}
