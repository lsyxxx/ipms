package com.abt.sys.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.exception.MissingRequiredParameterException;
import com.abt.common.model.R;
import com.abt.sys.service.EnumLibService;
import com.abt.testing.entity.EnumLib;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/sys/set")
public class OASettingController {

    private final EnumLibService enumLibService;

    public OASettingController(EnumLibService enumLibService) {
        this.enumLibService = enumLibService;
    }


    @PostMapping("/updateEnum")
    public R<Object> updateEnum(@Validated(value = {ValidateGroup.Save.class}) @RequestBody EnumLib enumLib) {
        enumLibService.updateEnumLib(enumLib);
        return R.success("添加成功");
    }

    @PostMapping("/createEnum")
    public R<Object> updateEnumLib(@Validated(value = {ValidateGroup.Save.class}) @RequestBody EnumLib enumLib) {
        enumLibService.createEnumLib(enumLib);
        return R.success("添加成功");
    }

    @GetMapping("/delEnum")
    public R<Object> deleteEnumLib(String id) {
        enumLibService.deleteEnumLib(id);
        return R.success("删除成功");
    }


    @GetMapping("/get/{ftypeid}")
    public R<List<EnumLib>> findEnumLibByType(@PathVariable String ftypeid) {
        if (StringUtils.isBlank(ftypeid)) {
            throw new MissingRequiredParameterException("枚举类型(ftypeid)");
        }
        final List<EnumLib> certEnums = enumLibService.findEnumLibsBy(ftypeid);
        return R.success(certEnums);
    }

    @GetMapping("/allEnum")
    public R<Map<String, List<EnumLib>>> findAllEnum() {
        final Map<String, List<EnumLib>> allEnum = enumLibService.findAllEnum();
        return R.success(allEnum);
    }

    /**
     * 一个新的证件类型Enum
     */
    @GetMapping("/cert/new")
    public R<EnumLib> newCertTypeEnumLib() {
        final EnumLib enumLib = enumLibService.newCertEnumLib();
        return R.success(enumLib);
    }


}
