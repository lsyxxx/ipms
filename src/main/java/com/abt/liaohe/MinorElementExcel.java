package com.abt.liaohe;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.camunda.bpm.model.cmmn.impl.instance.ApplicabilityRuleImpl;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Service
public class MinorElementExcel{

    private final RawDataRepository rawDataRepository;
    private final TestBaseTableRepository testBaseTableRepository;

    @Getter
    @Setter
    private File file;

    private Workbook workbook;
    private Sheet sheet;
    /**
     * 第一列作为header
     */
    @Getter
    @Setter
    private int headerCol = 0;

    /**
     * 数据起始行
     */
    @Getter
    @Setter
    private int startRow = 2;

    private List<String> header = new ArrayList<>();

    private void excelReader(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            workbook = WorkbookFactory.create(fis);
            sheet = workbook.getSheetAt(0);
        }
    }

    public MinorElementExcel(RawDataRepository rawDataRepository,  TestBaseTableRepository testBaseTableRepository) {
        super();
        this.rawDataRepository = rawDataRepository;
        this.testBaseTableRepository = testBaseTableRepository;
    }

    /**
     * 读取表头，第一列
     */
    public void readHeader() {
        for (Row row : sheet) {
            Cell cell = row.getCell(headerCol);
            header.add(row.getRowNum(), Util.getCellValueAsString(cell));
        }
    }

}
