package com.abt.sys.controller;

import com.abt.common.model.R;
import com.abt.sys.service.EnumLibService;
import com.abt.testing.entity.EnumLib;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/sys/enum")
public class EnumController {

    private final EnumLibService enumLibService;

    public EnumController(EnumLibService enumLibService) {
        this.enumLibService = enumLibService;
    }

    @GetMapping("/type")
    public R<List<EnumLib>>  findByType(String type) {
        final List<EnumLib> list = enumLibService.findEnumLibsBy(type);
        return R.success(list);
    }
}
