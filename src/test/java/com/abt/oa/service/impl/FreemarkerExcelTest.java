package com.abt.oa.service.impl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FreemarkerExcelTest {

    @Autowired
    private Configuration freemarkerConfig;

    @Test
    public void generateExcel() throws IOException, TemplateException {

        String outputFilePath = "E:\\fw_atd\\test.xls";

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("yearMonth", "2024-08");

        dataModel.put("ABTSummaryHeader", List.of("出勤", "流体1"));
        dataModel.put("ABTSummaryHeaderShort", List.of("LT1", "JX2"));

        //数据
        List<String> row1 = List.of("2", "刘宋菀", "X1", "X2", "X3", "X4", "X5", "X6", "X7", "X8", "X9");
        List<String> row2 = List.of("3", "送哈哈", "S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S99", "室内", "业务", "野外");
        dataModel.put("ABTList", List.of(row1, row2));

        Template template = freemarkerConfig.getTemplate("FW2003.ftl");
        template.process(dataModel, new FileWriter(outputFilePath));
    }


}