package com.abt.salary.service.impl;

import com.abt.salary.entity.SalaryMain;
import com.abt.salary.model.SalaryPreview;
import com.abt.salary.service.SalaryService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.metadata.ReadSheet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SalaryServiceImplTest {

    @Autowired
    private SalaryService salaryService;

    public static final String testFile = "C:\\Users\\Administrator\\Desktop\\salary_test.xlsx";

    @Test
    void tempExcelExtract() throws FileNotFoundException {
        final SalaryMain main = salaryService.createSalaryMain("2024-03", "ABT", "本月实发");

        File file = new File(testFile);
        try(InputStream inputStream = new FileInputStream(file)) {

            final List<ReadSheet> readSheets = EasyExcel.read(testFile).build().excelExecutor().sheetList();
            System.out.println("========== Sheet Info ===============");
            readSheets.forEach(s -> {
                System.out.println("sheet " + s.getSheetNo() + ": " + s.getSheetName());
            });
            final SalaryPreview salaryPreview = salaryService.previewSalaryDetail(inputStream, "2024年3月工资", main);
            System.out.println(salaryPreview.getTypedErrorMap());

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
}