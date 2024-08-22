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

        String outputFilePath = "E:\\fw_atd\\test.xlsx";

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("yearMonth", "2024-08");
        dataModel.put("summaryHeader", List.of("出勤", "流体1"));
        dataModel.put("summaryHeaderShort", List.of("LT1", "JX2"));
        Template template = freemarkerConfig.getTemplate("fwm2003.ftl");
        template.process(dataModel, new FileWriter(outputFilePath));

//        StringWriter stringWriter = new StringWriter();
//        template.process(dataModel, stringWriter);
//
//        try (InputStream is = new ByteArrayInputStream(stringWriter.toString().getBytes());
//             XSSFWorkbook workbook = new XSSFWorkbook(is);
//             OutputStream os = new FileOutputStream(outputFilePath)) {
//            workbook.write(os);
//        }
    }


}