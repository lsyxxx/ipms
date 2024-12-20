package com.abt.liaohe;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.aspectj.ConfigurableObject;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.abt.liaohe.Util.getCellValueAsString;
import static com.abt.liaohe.Util.removeBlank;

/**
 *
 */
@Service
public class ChloroformBitumenAExcel extends AbstractExcelHandler{
    private final RawDataRepository rawDataRepository;
    private final TestBaseTableRepository testBaseTableRepository;
    private final ChloroformBitumenARepository chloroformBitumenARepository;
    private final ConfigurableObject configurableObject;



    private Sheet currentSheet;
    public ChloroformBitumenAExcel(RawDataRepository rawDataRepository, TestBaseTableRepository testBaseTableRepository,
                                   ChloroformBitumenARepository chloroformBitumenARepository, ConfigurableObject configurableObject) {
        super(rawDataRepository, testBaseTableRepository);
        this.rawDataRepository = rawDataRepository;
        this.testBaseTableRepository = testBaseTableRepository;
        this.chloroformBitumenARepository = chloroformBitumenARepository;
        this.configurableObject = configurableObject;
    }




    @Override
    void readHeader() {
        //第三行作为header;
        currentSheet = this.getSheet();
        List<String> header = new ArrayList<>();
        Row row = currentSheet.getRow(2);
        for (Cell cell : row) {
            if (cell.getColumnIndex() > 50) {
                break;
            }
            String value = removeBlank(Util.getCellValueAsString(cell));
            header.add(value);
        }
        //验证下是否是Header
        if (header.contains("序号") || header.contains("检测编号")) {
            this.setHeader(header);
        } else {
            throw new RuntimeException("表头错误；未包含序号/检测编号列");
        }
    }

    @Override
    void readAndSaveRawData() {
        //从第4
        int start = 3;
        for (Row row : this.getSheet()) {
            if (row.getRowNum() < start) {
                continue;
            }
            List<RawData> cached = new ArrayList<>();
            final boolean isEmpty = Util.validateEmptyRow(row);
            if (isEmpty) {
                continue;
            }
            for (Cell cell : row) {
                final boolean v = this.validateCell(cell, this.getHeader().size());
                if (!v) {
                    continue;
                }
                String cellValue = getCellValueAsString(cell);
                cellValue = removeBlank(cellValue);
                RawData rawData = new RawData();
                rawData.setTestValue(cellValue);
                rawData.setTestName(this.getHeader().get(cell.getColumnIndex()));
                rawData.setColIdx(cell.getColumnIndex());
                rawData.setRowIdx(cell.getRowIndex());
                rawData.setReportName(this.getFile().getName());
                cached.add(rawData);
            }
            rawDataRepository.saveAllAndFlush(cached);
        }
    }

    @Override
    void afterSaveRawData() {
        rawDataRepository.updateTestName("岩矿鉴定后定名", "岩性");
        rawDataRepository.updateTestName("岩心编号", "样品编号");
        rawDataRepository.updateTestName("井名", "井号");
        rawDataRepository.updateTestName("样号", "样品编号");
        rawDataRepository.updateTestName("井段", "深度");
        rawDataRepository.updateTestName("岩性定名", "岩性");
        rawDataRepository.updateTestName("送样编号", "样品编号");
        rawDataRepository.updateTestName("井深（m）", "深度");
        rawDataRepository.updateTestName("地层名称", "层位");
        rawDataRepository.deleteEmptyTestValue();
    }

    @Override
    boolean supportFile(File file) {
        return (file.getName().contains("沥青")  && file.getName().contains("A")) || file.getName().contains("氯仿") && Util.isExcelFile(file) ;
    }

    @Override
    void saveExcelData(File file) {
        saveData(file);
        writeBase(file);
    }


    public void setTestDate(String reportName, LocalDate startDate, LocalDate endDate) {
        TestBaseTable tbt = new TestBaseTable();
        tbt.setReportName(reportName);
        tbt.setTestDateStart(startDate);
        tbt.setTestDateEnd(endDate);
        testBaseTableRepository.save(tbt);
    }

    private void writeBase(File file) {
        final TestBaseTable testBase = testBaseTableRepository.findByReportName(file.getName());
        if (testBase == null) {
            System.out.println("未导入" + file.getName() + "基础数据");
            return;
        }
        final List<ChloroformBitumenA> list = chloroformBitumenARepository.findByReportName(file.getName());
        String dateStart = Util.handleTestDate(testBase.getTestDateStart());
        String dateEnd = Util.handleTestDate(testBase.getTestDateEnd());
        String sampleBatch = Util.handleSampleNo(testBase.getTestDateStart(), testBase.getTestDateEnd());
        list.forEach(chl -> {
            chl.setTestDateStart(dateStart);
            chl.setTestDateEnd(dateEnd);
            chl.setSampleBatch(sampleBatch);
        });
        chloroformBitumenARepository.saveAllAndFlush(list);
    }

    public void saveData(File file) {
        chloroformBitumenARepository.deleteByReportName(file.getName());
        final List<RawData> rawData = rawDataRepository.findByReportName(file.getName());
        final Map<Integer, List<RawData>> rowMap = rawData.stream().collect(Collectors.groupingBy(RawData::getRowIdx, TreeMap::new, Collectors.toList()));
        List<ChloroformBitumenA> cached = new ArrayList<>();
        for(Map.Entry<Integer, List<RawData>> entry : rowMap.entrySet()) {
            List<RawData> row = entry.getValue();
            ChloroformBitumenA ch = new ChloroformBitumenA(row);
            ch.setReportName(file.getName());
            ch.handleData();
            cached.add(ch);
        }
        chloroformBitumenARepository.saveAllAndFlush(cached);
    }
}
