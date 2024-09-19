package com.abt.oa.service.impl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import java.io.*;
import java.util.ArrayList;
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

        String outputFilePath = "E:\\fw_atd\\test.xlsx";

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


    @Test
    public void xlsxModel() throws IOException, TemplateException {
        String outputPath = "E:\\fw_atd\\output.xlsx";
        Map<String, Object> data = new HashMap<>();
        data.put("header1", "Name");
        data.put("header2", "Age");
        data.put("header3", "Location");

        List<Map<String, String>> rows = new ArrayList<>();
        Map<String, String> row1 = new HashMap<>();
        row1.put("col1", "John");
        row1.put("col2", "30");
        row1.put("col3", "New York");
        rows.add(row1);

        Map<String, String> row2 = new HashMap<>();
        row2.put("col1", "Alice");
        row2.put("col2", "25");
        row2.put("col3", "Los Angeles");
        rows.add(row2);

        data.put("rows", rows);

        // 加载 Freemarker 模板
        Template template = freemarkerConfig.getTemplate("test.ftl");

        // 使用 Freemarker 处理模板并生成字符串
        String xmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, data);

        // 将生成的XML内容转为Excel文件
        try (Workbook workbook = new XSSFWorkbook()) {
            // 可以使用 POI 解析和处理 xmlContent 生成 Excel 表格
            // 示例中略去细节
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                workbook.write(fos);
            }
        }
    }


    public static void main(String[] args) {



    }


}