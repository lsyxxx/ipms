package com.abt.oa.service.impl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Map;

/**
 *
 */
@Service
public class FreemarkerExcel {


    private final Configuration freemarkerConfig;

    public FreemarkerExcel(Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    public void generateExcel(Map<String, Object> dataModel, String outputFilePath) throws IOException, TemplateException {
        Template template = freemarkerConfig.getTemplate("fwm2003.ftl");

//        StringWriter stringWriter = new StringWriter();
//        template.process(dataModel, stringWriter);

//        try (InputStream is = new ByteArrayInputStream(stringWriter.toString().getBytes());
//             XSSFWorkbook workbook = new XSSFWorkbook(is);
//             OutputStream os = new FileOutputStream(outputFilePath)) {
//            workbook.write(os);
//        }
    }


}
