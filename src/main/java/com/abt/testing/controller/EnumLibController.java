package com.abt.testing.controller;

import com.abt.testing.entity.EnumLib;
import com.abt.common.model.R;
import com.abt.testing.service.EnumLibService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/enum")
public class EnumLibController {

    private final EnumLibService enumLibService;

    private final Map<String, List<EnumLib>> allEnumMap;

    public EnumLibController(EnumLibService enumLibService,@Qualifier("allEnumMap") Map<String, List<EnumLib>> allEnumMap) {
        this.enumLibService = enumLibService;
        this.allEnumMap = allEnumMap;
    }


    @GetMapping("/all")
    public R<Map<String, List<EnumLib>>> findAllEnum() {
        return R.success(allEnumMap);
    }

}
