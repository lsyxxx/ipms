package com.abt.liaohe;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.camunda.bpm.model.cmmn.impl.instance.ApplicabilityRuleImpl;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class MinorElementExcel{

    private final RawDataRepository rawDataRepository;
    private final TestBaseTableRepository testBaseTableRepository;
    private final MinorElementRepository minorElementRepository;

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
     * 数据起始行, 默认从第二行开始
     */
    @Getter
    @Setter
    private int startRow = 2;

    private List<String> header = new ArrayList<>();


    public MinorElementExcel(RawDataRepository rawDataRepository, TestBaseTableRepository testBaseTableRepository, MinorElementRepository minorElementRepository) {
        this.rawDataRepository = rawDataRepository;
        this.testBaseTableRepository = testBaseTableRepository;
        this.minorElementRepository = minorElementRepository;
    }

    private void excelReader(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            workbook = WorkbookFactory.create(fis);
            sheet = workbook.getSheetAt(0);
        }
    }

    public void readAndSaveRawData() throws IOException {
        excelReader(this.file);
        rawDataRepository.deleteByReportName(this.file.getName());
        readData();
        afterReadExcel();

    }

    private void afterReadExcel() {
        rawDataRepository.deleteEmptyData();
        rawDataRepository.deleteRemarkContent();
//        rawDataRepository.deleteIndexCol();
        rawDataRepository.deleteEmptyTestValue();

    }


    public void handleRawData() {
        rawDataRepository.updateTestName("岩矿鉴定后定名", "岩性");
        rawDataRepository.updateTestName("岩心编号", "样品编号");
        rawDataRepository.updateTestName("井名", "井号");
        rawDataRepository.updateTestName("样号", "样品编号");
        rawDataRepository.updateTestName("井段", "深度");

    }




    /**
     * 读取表头，第一列
     */
    private void readHeader() {
        for (Row row : sheet) {
            if (row.getRowNum() < this.startRow) {
                continue;
            }
            Cell cell = row.getCell(headerCol);
            header.add(Util.removeBlank(Util.getCellValueAsString(cell)));
        }
    }

    private void readData() {
        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                List<RawData> cached = new ArrayList<>();
                String header = "";
                for (Cell cell : row) {
                    if (cell.getColumnIndex() > 50) {
                        break;
                    }
                    //第一列是表头
                    if (cell.getColumnIndex() == 0) {
                        header = Util.getCellValueAsString(cell);
                        continue;
                    }
                    String testName = header;
                    String cellStr = Util.getCellValueAsString(cell);
                    Util.removeBlank(cellStr);
                    RawData rawData = new RawData();
                    rawData.setSheetName(sheet.getSheetName());
                    rawData.setTestValue(cellStr);
                    rawData.setTestName(testName);
                    rawData.setColIdx(cell.getColumnIndex());
                    rawData.setRowIdx(cell.getRowIndex());
                    rawData.setReportName(this.file.getName());
                    cached.add(rawData);
                }
                rawDataRepository.saveAllAndFlush(cached);
            }
        }
    }

    private boolean isElement(RawData rawData) {
        return rawData.getTestName() != null && !rawData.getTestName().isEmpty() && rawData.getTestName().matches("[a-zA-Z]+");
    }

    private void writeBase() {
        final TestBaseTable testBase = testBaseTableRepository.findByReportName(this.file.getName());
        if (testBase == null) {
            System.out.println("未导入" + this.file.getName() + "基础数据");
            return;
        }
        final List<MinorElement> list = minorElementRepository.findByReportName(this.file.getName());
        String dateStart = Util.handleTestDate(testBase.getTestDateStart());
        String dateEnd = Util.handleTestDate(testBase.getTestDateEnd());
        String sampleBatch = Util.handleSampleNo(testBase.getTestDateStart(), testBase.getTestDateEnd());
        list.forEach(minorElement -> {
            minorElement.setTestDateStart(dateStart);
            minorElement.setTestDateEnd(dateEnd);
            minorElement.setSampleBatch(sampleBatch);
        });
        minorElementRepository.saveAllAndFlush(list);
    }

    public void saveMinorElementData() {
        this.saveMinorElement();
        this.writeBase();
    }

    private void saveMinorElement() {
        minorElementRepository.deleteByReportName(this.file.getName());
        final List<RawData> rawData = rawDataRepository.findByReportName(this.file.getName());
        final Map<String, List<RawData>> groupBySheetName = rawData.stream().collect(Collectors.groupingBy(RawData::getSheetName, TreeMap::new, Collectors.toList()));
        for (Map.Entry<String, List<RawData>> entry : groupBySheetName.entrySet()) {
            List<MinorElement> cached = new ArrayList<>();
            List<RawData> oneSheet = entry.getValue();
            //一个sheet页
            final TreeMap<Integer, List<RawData>> groupByColMap = oneSheet.stream().collect(Collectors.groupingBy(RawData::getColIdx, TreeMap::new, Collectors.toList()));
            for (Map.Entry<Integer, List<RawData>> oneCol : groupByColMap.entrySet()) {
                //一列
                List<RawData> colData = oneCol.getValue();
                for (RawData col : colData) {
                    //一个参数
                    if (isElement(col)) {
                        MinorElement base = new MinorElement(colData);
                        base.setData(col);
                        base.handleData();
                        base.setReportName(this.file.getName());
                        cached.add(base);
                    }
                }
            }
            minorElementRepository.saveAllAndFlush(cached);
        }
    }

    public boolean isMinor() {
        return this.file.getName().contains("微量") ||  this.file.getName().contains("稀土");
    }

}
