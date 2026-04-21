package com.abt.instrument.controller;

import com.abt.chkmodule.entity.Instrument;
import com.abt.chkmodule.model.SimpleCheckModule;
import com.abt.chkmodule.service.InstrumentService;
import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.model.SaveMode;
import com.abt.testing.model.InstrumentRequestForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/instrument")
public class InstrumentController {

    private final InstrumentService instrumentService;

    public InstrumentController(InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }
    /**
     * 设备-列表条件查询
     */
    @PostMapping("/page")
    public R<Page<Instrument>> findInstrumentPage(@RequestBody InstrumentRequestForm form) {
        form.forcePaged();
        Pageable pageable = form.createDefaultPageable();
        Page<Instrument> page = instrumentService.findInstrumentPage(
                form.getQuery(),
                form.getTypes(),
                form.getStatus(),
                form.getUseDepts(),
                pageable
        );
        return R.success(page);
    }

    /**
     /**
     * 设备-生成设备编号
     *
     * @param typePrefix 设备分类
     * @param deptPrefix 使用部门
     */
    @GetMapping("/generateCode")
    public R<String> generateInstrumentCode(String typePrefix, String deptPrefix) {
        String newCode = instrumentService.generateInstrumentCode(typePrefix, deptPrefix);
        return R.success(newCode);
    }

    /**
     * 设备-保存/编辑
     */
    @PostMapping("/save")
    public R<Object> saveInstrument(@Validated(ValidateGroup.Save.class) @RequestBody Instrument instrument) {
        instrument.setSaveMode(SaveMode.SAVE);
        instrumentService.saveInstrument(instrument);
        return R.success("操作成功");
    }

    /**
     * 设备管理-查看详情
     */
    @GetMapping("/find")
    public R<Instrument> findInstrumentDetail(String id) {
        return R.success(instrumentService.findInstrumentById(id));
    }

    /**
     * 设备-查询指定设备关联的检测项目
     * @param instrumentId 设备id
     */
    @GetMapping("/modules/list")
    public R<List<SimpleCheckModule>> findModulesByInstrumentId(String instrumentId) {
        return R.success(instrumentService.findModulesByInstrumentId(instrumentId));
    }

    /**
     * 设备-暂存
     */
    @PostMapping("/saveDraft")
    public R<Object> draftInstrument(@Validated(ValidateGroup.Temp.class) @RequestBody Instrument instrument) {
        instrumentService.saveInstrument(instrument);
        return R.success("暂存成功");
    }

    /**
     * 设备-删除暂存草稿
     */
    @GetMapping("/deleteDraft")
    public R<Object> deleteDraftInstrument(String id) {
        instrumentService.deleteTempInstrument(id);
        return R.success("草稿删除成功");
    }
}
