package com.abt.liaohe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DataExportTest {

    @Autowired
    private DataExport dataExport;

    public static final String dir = "F:\\00平台资料汇总\\辽河数据\\辽河2024\\2024-物性\\";

    @Test
    void readRawData() throws IOException {
        dataExport.readRawData(dir);
    }

    @Test
    void writeRockData() throws IOException {
        dataExport.writeRockAnalysisData(dir);
    }

    @Test
    void writeExcel() throws IOException {
        String fileName = "F:\\00平台资料汇总\\辽河数据\\辽河2024\\岩石分析-2020-2024.xlsx";
        dataExport.writeExcel(dir, fileName);
    }

    @Test
    void handleData() {
        dataExport.handleData();
    }


    //岩石碳酸盐含量
    void handleRockCarbonateMeasu() {

    }
}