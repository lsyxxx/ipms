package com.abt.wf.controller;

import com.abt.common.exception.MissingRequiredParameterException;
import com.abt.common.model.R;
import com.abt.sys.model.entity.FlowSetting;
import com.abt.wf.service.FlowSettingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.Flow;

import static com.abt.wf.config.Constants.SETTING_TYPE_RBS_COPY;

/**
 * 流程设置
 */
@RestController
@Slf4j
@RequestMapping("/wf/set")
public class SettingController {

    private final FlowSettingService flowSettingService;

    public SettingController(FlowSettingService flowSettingService) {
        this.flowSettingService = flowSettingService;
    }

    /**
     * 流程参数设置
     */
    @PostMapping("/update")
    public R<Object> set(@RequestBody FlowSetting setting) {
        flowSettingService.set(setting);
        return R.success();
    }

    @GetMapping("/all")
    public R<List<FlowSetting>> loadAll() {
        final List<FlowSetting> all = flowSettingService.findAll();
        return R.success(all, all.size());
    }

    @GetMapping("/{id}")
    public R<FlowSetting> load(@PathVariable String id) {
        ensureEntityId(id);
        final FlowSetting entity = flowSettingService.findById(id);
        return R.success(entity);
    }

    @GetMapping("/del/{id}")
    public R<Object> delete(@PathVariable String id) {
        ensureEntityId(id);
        flowSettingService.delete(id);
        return R.success("删除成功!");
    }

    /**
     * 费用报销流程默认抄送人
     */
    @PostMapping("/rbs/copyusers")
    public R<Object> saveReimburseDefaultCopyUserList(@RequestBody List<FlowSetting> list) {
        flowSettingService.deleteAndSaveBatch(list, SETTING_TYPE_RBS_COPY);
        return R.success("更新成功");
    }

    @GetMapping("/find/{type}")
    public R<List<FlowSetting>> findByType(@PathVariable String type) {
        final List<FlowSetting> list = flowSettingService.findByTypeOrderByKeyAsc(type);
        return R.success(list);
    }

    public void ensureEntityId(String id) {
        if (StringUtils.isNotBlank(id)) {
            throw new MissingRequiredParameterException("流程设置实体id(FlowSetting:id)");
        }
    }

}
