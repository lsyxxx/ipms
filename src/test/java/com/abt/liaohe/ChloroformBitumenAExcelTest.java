package com.abt.liaohe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChloroformBitumenAExcelTest {
    @Autowired
    private ChloroformBitumenAExcel excel;

    @Test
    void copyFiles() throws IOException {
        excel.copyFiles(null, "F:\\00平台资料汇总\\辽河数据\\氯仿沥青报告\\");
        System.out.println("复制文件结束");
    }

    void readData() {

    }

}