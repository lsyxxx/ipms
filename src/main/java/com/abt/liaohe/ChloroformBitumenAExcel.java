package com.abt.liaohe;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.aspectj.ConfigurableObject;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
            header.add(Util.getCellValueAsString(cell));
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

    }

    @Override
    boolean supportFile(File file) {
        return file.getName().contains("沥青") && file.getName().contains("氯仿") && Util.isExcelFile(file) ;
    }
}
