package com.abt.liaohe;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 常规物性excel
 */
public class RockAnalysisExcel {
    @Getter
    @Setter
    private File file;
    private Workbook workbook;
    private Sheet sheet;
    private String testDate = "";
    /**
     * 数据起始行
     */
    private int startRow = 0;

    //表头起始行
    private int headerRowStart = 0;
    //表头结束行
    private int headerRowEnd = 0;

    /**
     * 是否是现在的模板
     */
    private boolean isCurrentTemplate = false;;
    //是否多行表头
    private boolean isMultiHeader = false;

    public List<String> headerContains = List.of(
            "检测编号",	"样品编号",	"井号",	"深度",	"井深", "层位",	"岩性",	"孔隙度", "渗透率", "饱和度", "水平", "垂直","岩石密度", "油", "水",	"备注"
            );

    private List<String> headerRow1 = new ArrayList<>();
    private List<String> headerRow2 = new ArrayList<>();
    private List<String> headerRow = new ArrayList<>();

    public void read() throws IOException {
        excelReader(file);
        mergedCells();
        System.out.printf("----| 数据起始行：%d%n", this.startRow);
        readHeader();
        System.out.printf("----| 表头：%s%n", this.headerRow);

    }

    /**
     * 合并单元格
     */
    private void mergedCells() {
        List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
        for (CellRangeAddress mergedRegion : mergedRegions) {
            Row row = sheet.getRow(mergedRegion.getFirstRow());
            Cell cell = row.getCell(mergedRegion.getFirstColumn());
            String value = cell.getStringCellValue();
            System.out.printf("----| 合并单元格: startRow:%d, endRow: %d, startCol: %d, endCol: %d, value: %s%n",
                    mergedRegion.getFirstRow(),
                    mergedRegion.getLastRow(),
                    mergedRegion.getFirstColumn(),
                    mergedRegion.getLastColumn(),
                    cell.getStringCellValue()
            );
            if (value.contains("检测编号")) {
                this.isMultiHeader = true;
                this.headerRowStart = mergedRegion.getFirstRow();
                this.headerRowEnd = mergedRegion.getLastRow();
            }
        }
        this.startRow = headerRowEnd + 1;
    }


    private void excelReader(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            workbook = WorkbookFactory.create(fis);
            sheet = workbook.getSheetAt(0);
        }

    }


    public static final String reportHeader = "物性报告";
    public static final String testDateHeader = "检测日期";

    private String filterHeader(String cellValue) {
        return headerContains.stream().filter(cellValue::contains).findAny().orElse(cellValue);
    }

    private void readHeader() {
        for(Row row : sheet) {
            int rowNum = row.getRowNum();
            if (rowNum >= startRow) {
                return;
            }
            System.out.printf("--| read row: %d\n", rowNum);
            for (Cell cell : row) {
                if (cell == null) {
                    continue;
                }
                String cellValue = getCellValueAsString(cell);
                cellValue = removeBlank(cellValue);
                int colIdx = cell.getColumnIndex();
                if (StringUtils.isNotBlank(getCellValueAsString(cell))) {
                    System.out.printf("----| read cell: %d, data: %s%n", cell.getColumnIndex(), getCellValueAsString(cell));
                }

                if (cellValue.contains(reportHeader)) {
                    //标题行
                    continue;
                }
                if (cellValue.contains(testDateHeader)) {
                    //检测日期
                    testDate = cellValue.replace(testDateHeader, "");
                    testDate = testDate.replace(":", "");
                    testDate = testDate.replace("：", "");
                    testDate = testDate.replace(" ", "");
                }
               if (row.getRowNum() == headerRowStart) {
                   headerRow1.add(colIdx, cellValue);
               }
               if (row.getRowNum() == headerRowEnd) {
                   headerRow2.add(colIdx, cellValue);
               }
            }
        }
        System.out.println("-------合并表头-------");

        //合并表头，将headerRow1, headerRow2 合并
        if (isMultiHeader) {
            int len = Math.max(headerRow1.size(), headerRow2.size());
            for (int i = 0; i < len; i++) {
                this.headerRow.add(i, headerRow1.get(i) + headerRow2.get(i));
            }
        }
        System.out.printf("表头2 %s%n", this.headerRow);
    }


    /**
     * 读取数据
     */
    private void readData() {
        System.out.println("--| read data start");
        for(Row row : sheet) {
            int rowNum = row.getRowNum();
            if (rowNum < startRow) {
                continue;
            }
            for (Cell cell : row) {
                String cellValue = cell.getStringCellValue();
                int colNum = cell.getColumnIndex();


            }
        }
    }

    private void readDataRow(Row row) {

    }

    // 移除所有空白字符，包括空格、回车、换行、制表符等
    private String removeBlank(String string) {
        return string.replace("\\s+", "");
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString(); // 日期格式可以自行调整
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return "";
        }
    }

}
