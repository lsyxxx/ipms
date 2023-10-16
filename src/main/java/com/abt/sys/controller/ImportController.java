package com.abt.sys.controller;

import com.abt.flow.repository.FlowSettingRepository;
import com.abt.flow.service.FlowInfoService;
import com.abt.sys.service.ImportData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/test/import")
public class ImportController {

    private final FlowSettingRepository flowSettingRepository;
    private final FlowInfoService flowInfoService;

    public ImportController(FlowSettingRepository flowSettingRepository, FlowInfoService flowInfoService) {
        this.flowSettingRepository = flowSettingRepository;
        this.flowInfoService = flowInfoService;
    }

//    @GetMapping("/defaultAuditors")
    public void importDefaultAuditors() {
        ImportData importData = new ImportData(flowSettingRepository);
        importData.importFlowSettings();
    }

    @GetMapping("/defpng")
    public void generateFlowPng() throws IOException {
        String def = "50ae3f59-6816-11ee-87ab-a497b12f53fd";
        flowInfoService.generateProcessDefPng(def);
    }

}
