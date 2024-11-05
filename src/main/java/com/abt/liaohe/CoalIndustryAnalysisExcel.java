package com.abt.liaohe;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 煤工业分析
 */
@NoArgsConstructor
public class CoalIndustryAnalysisExcel {
    private RawDataRepository rawDataRepository;

    private File excel;
    private Workbook workbook;
    private Sheet sheet;

    public CoalIndustryAnalysisExcel(File excel, RawDataRepository rawDataRepository) {
        this.excel = excel;
        this.rawDataRepository = rawDataRepository;
    }

    public CoalIndustryAnalysisExcel build() throws IOException {
        excelReader(this.excel);
        return this;
    }


    private void excelReader(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            workbook = WorkbookFactory.create(fis);
            sheet = workbook.getSheetAt(0);
        }
    }

    public static final String testDateHeader = "检测日期";
    public static final String indexHeader = "序号";


    /**
     * 读取excel数据
     * @param file excel文件
     */
    public void readExcel(File file) {
        Util.recognizeDataRow(sheet);


    }

    /**
     * 从文件中写入tmp_raw_data
     */
    public void writeRawData() {

    }

    /**
     * 从tmp_raw_data中获取数据
     */
    public void readRawData(String fileName) {

    }



}
