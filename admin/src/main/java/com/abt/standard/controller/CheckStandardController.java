package com.abt.standard.controller;

import com.abt.chkmodule.entity.CheckStandard;
import com.abt.chkmodule.model.StandardItemModuleUnitDTO;
import com.abt.common.config.ValidateGroup;
import com.abt.common.model.SaveMode;
import com.abt.standard.model.CheckStandardRequestForm;
import com.abt.chkmodule.service.CheckStandardService;
import com.abt.common.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/standard")
public class CheckStandardController {

    private final CheckStandardService checkStandardService;

    public CheckStandardController(CheckStandardService checkStandardService) {
        this.checkStandardService = checkStandardService;
    }

    /**
     * 标准-列表条件查询
     */
    @PostMapping("/page")
    public R<Page<CheckStandard>> findStandardPage(@RequestBody CheckStandardRequestForm form) {
        form.forcePaged();
        Pageable pageable = form.createDefaultPageable();
        Page<CheckStandard> page = checkStandardService.findStandardPage(
                form.getQuery(),
                form.getStatus(),
                form.getLevels(),
                pageable
        );
        return R.success(page);
    }

    /**
     * 标准-查询所有不同的标准等级
     */
    @GetMapping("/levels/options")
    public R<List<String>> findLevelOptions() {
        List<String> levels = checkStandardService.findDistinctLevels();
        return R.success(levels);
    }
    /**
     * 标准-新增/编辑一个标准
     */
    @PostMapping("/save")
    public R<Object> saveStandard(@Validated(ValidateGroup.Save.class) @RequestBody CheckStandard checkStandard) {
        checkStandard.setSaveMode(SaveMode.SAVE);
        checkStandardService.saveStandard(checkStandard);
        return R.success("保存成功");
    }

    /**
     * 标准-查看指定标准详情
     */
    @GetMapping("/find")
    public R<CheckStandard> findStandardDetail(String id) {
        return R.success(checkStandardService.findStandardById(id));
    }

    /**
     * 标准-查询指定标准关联的检测子参数列表及检测项目
     */
    @GetMapping("/items/list")
    public R<List<StandardItemModuleUnitDTO>> findUnitItemsModulesByStandardId(String id) {
        return R.success(checkStandardService.findUnitItemsModulesByStandardId(id));
    }

    /**
     * 标准-暂存
     */
    @PostMapping("/saveDraft")
    public R<Object> draftStandard(@Validated(ValidateGroup.Temp.class) @RequestBody CheckStandard checkStandard) {
        checkStandardService.saveStandard(checkStandard);
        return R.success("暂存成功");
    }
}