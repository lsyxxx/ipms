package com.abt.liaohe;

import com.abt.common.model.R;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.abt.liaohe.Util.*;

/**
 * 常规物性excel
 */
@Slf4j
public class RockAnalysisExcel {
    private RawDataRepository rawDataRepository;
    private RockAnalysisDataRepository rockAnalysisDataRepository;
    @Getter
    @Setter
    private File file;
    private Workbook workbook;
    private Sheet sheet;
    /**
     * 当前文件表格数据
     */
    private List<List<String>> table;
    private String testDate = "";
    /**
     * 数据起始行
     */
    private int startRow = 0;

    //表头起始行
    private int headerRowStart = 0;
    //表头结束行，单行表头则是3
    private int headerRowEnd = 0;

    /**
     * 是否是现在的模板
     */
    private boolean isCurrentTemplate = false;
    ;
    //是否多行表头
    private boolean isMultiHeader = false;

    public static final List<String> headerContains = List.of(
            "检测编号", "样品编号", "井号", "深度", "井深", "层位", "岩性", "岩心描述", "孔隙度", "渗透率", "饱和度", "水平", "垂直", "岩石密度", "油", "水", "备注"
    );

    public static final List<String> base = List.of(
            "检测编号", "样品编号", "井号", "深度", "井深", "层位", "岩性", "岩心描述"
    );

    private List<String> headerRow1 = new ArrayList<>();
    private List<String> headerRow2 = new ArrayList<>();
    private List<String> headerRow = new ArrayList<>();
    private Map<Integer, String> mergedHeader = new HashMap<>();


    public RockAnalysisExcel(RawDataRepository rawDataRepository, RockAnalysisDataRepository rockAnalysisDataRepository) {
        this.rawDataRepository = rawDataRepository;
        this.rockAnalysisDataRepository = rockAnalysisDataRepository;
    }

    /**
     * 1. 找到数据的第一行
     * 2. 数据行前所有行，同列合并作为表头
     * 3. 处理表头
     */
    public void extractToDb() throws IOException {
        rawDataRepository.deleteByReportName(file.getName());
        excelReader(file);
        recognizeDataRow();
        System.out.printf("数据起始行：%d%n", this.startRow);
        mergeHeader();
        readData();
        afterRawData();
    }

    /**
     * 将数据行前所有行的同列合并
     */
    private void mergeHeader() {
        for (int i = 0; i < this.startRow; i++) {
            Row row = sheet.getRow(i);
            for(Cell cell: row) {
                String value = this.mergedHeader.getOrDefault(cell.getColumnIndex(), StringUtils.EMPTY);
                String cellValue = getCellValueAsString(cell);
                if (cellValue.contains(testDateHeader)) {
                    //检测日期
                    testDate = cellValue.replace(testDateHeader, "");
                    testDate = testDate.replace(":", "");
                    testDate = testDate.replace("：", "");
                    testDate = testDate.replace(" ", "");
                    continue;
                }
                cellValue = removeBlank(cellValue);
                mergedHeader.put(cell.getColumnIndex(), value + cellValue);
            }
        }
        System.out.printf("合并后的headerMap: %s%n", mergedHeader);

    }


    private void mergeHeader2() {
        //合并表头，将headerRow1, headerRow2 合并
        if (isMultiHeader) {
            //删除可能存在的空表头行
            final boolean h1 = validateEmpty(this.headerRow1);
            final boolean h2 = validateEmpty(this.headerRow2);
            if (h1) {
                //headerRow1全空
                this.headerRow = this.headerRow2;
                return;
            }
            if (h2) {
                //headerRow2全空
                this.headerRow = this.headerRow1;
                return;
            }

            int len = Math.max(this.headerRow1.size(), this.headerRow2.size());
            padList(len, this.headerRow1);
            padList(len, this.headerRow2);
            for (int i = 0; i < len; i++) {
                this.headerRow.add(i, headerRow1.get(i) + headerRow2.get(i));
            }
        } else {
            this.headerRow = this.headerRow1;
        }

    }

    private void padList(int size, List<String> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        if (list.size() < size) {
            while (list.size() < size) {
                list.add("");
            }
        }
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
        if (isMultiHeader) {
            this.startRow = headerRowEnd + 1;
        }
    }


    private void excelReader(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            workbook = WorkbookFactory.create(fis);
            sheet = workbook.getSheetAt(0);
        }

    }


    public static final String reportHeader = "物性报告";
    public static final String testDateHeader = "检测日期";
    public static final String indexHeader = "序号";

    private String filterHeader(String cellValue) {
        return headerContains.stream().filter(cellValue::contains).findAny().orElse(cellValue);
    }

    private void readHeader() {
        for (Row row : sheet) {
            int rowNum = row.getRowNum();
            if (rowNum > 1000) {
                return;
            }

            for (Cell cell : row) {
                if (cell == null) {
                    continue;
                }
                if (cell.getColumnIndex() > 50) {
                    return;
                }
                String cellValue = getCellValueAsString(cell);
                cellValue = removeBlank(cellValue);
                int colIdx = cell.getColumnIndex();
                if (StringUtils.isNotBlank(cellValue)) {
//                    System.out.printf("----| read cell: %d, data: %s%n", cell.getColumnIndex(), cellValue);
                }

                if (cellValue.contains(reportHeader)) {
                    //标题行
                    break;
                }
                if (cellValue.contains(testDateHeader)) {
                    //检测日期
                    testDate = cellValue.replace(testDateHeader, "");
                    testDate = testDate.replace(":", "");
                    testDate = testDate.replace("：", "");
                    testDate = testDate.replace(" ", "");
                    break;
                }
                if (cellValue.contains(indexHeader)) {
                    //检测编号
                    if (this.startRow == 0) {
                        //说明没有合并表头
                        this.startRow = cell.getRowIndex() + 1;
                    }
                }
                //表头：一般就两行
                if (isMultiHeader) {
                    if (row.getRowNum() == headerRowStart) {
                        headerRow1.add(colIdx, cellValue);
                    }
                    if (row.getRowNum() == headerRowEnd) {
                        headerRow2.add(colIdx, cellValue);
                    }
                } else {
                    this.headerRow1.add(colIdx, cellValue);
                }

            }
        }
    }

    /**
     * 寻找数据起始行
     * 原理：找到包含AJC/JC检测编号的第一行就是数据的第一行
     */
    private void recognizeDataRow() {
        //只观察前10行
        int range = 10;
        final int lastRowNum = sheet.getLastRowNum();
        //防止超出
        if (range > lastRowNum) {
            range = lastRowNum;
        }
        for (int i = 0; i < range; i++) {
            Row row = sheet.getRow(i);
            for(Cell cell : row) {
                //
                String cellValue = getCellValueAsString(cell);
                if (cellValue.contains("AJC") || cellValue.contains("JC"))  {
                    //第一次找到数据行
                    this.startRow = row.getRowNum();
                }
            }
            if (this.startRow > 0) {
                break;
            }
        }
    }

    /**
     * 判断表头
     */
    private void recognizeHeader() {
        //合并的表头不用处理，在mergedCell()处理过了
        if (isMultiHeader) {
            return;
        }
        //只观察前10行
        int range = 10;
        final int lastRowNum = sheet.getLastRowNum();
        //防止超出
        if (range > lastRowNum) {
            range = lastRowNum;
        }

        rowLoop: for (Row row : sheet) {
            if (row.getRowNum() > range) {
                break;
            }
            for (Cell cell : row) {
                String cellValue = getCellValueAsString(cell);
                if (cellValue.contains("AJC") || cellValue.contains("JC"))  {
                    //第一次找到数据行
                    this.startRow = row.getRowNum();
                    break rowLoop;
                }
            }
        }
        if (this.startRow > 0) {
            System.out.printf("找到数据起始行: %d%n", this.startRow);
        } else {
            System.out.println("没找到数据起始行!");
        }
    }


    /**
     * 读取数据
     */
    private void readData() {
        System.out.println("--| read data start");
        int len = this.mergedHeader.size();
        table = new ArrayList<>(len);
        for (Row row : sheet) {
            List<RawData> cached = new ArrayList<>();
            List<String> dataRow = new ArrayList<>();
            int rowNum = row.getRowNum();
            if (rowNum < startRow) {
                continue;
            }
            if (rowNum > 1000) {
                //设个边界，不会超过1000
                break;
            }
            for (Cell cell : row) {
                if (cell.getColumnIndex() > 50) {
                    //设个边界
                    break;
                }
                String cellValue = getCellValueAsString(cell);
                cellValue = removeBlank(cellValue);
                dataRow.add(cellValue);
                final RawData rawData = writeCellRawData(cell.getColumnIndex(), cell.getRowIndex(), cellValue, this.testDate);
                if (rawData != null) {
                    cached.add(rawData);
                }
            }
            table.add(dataRow);
            rawDataRepository.saveAllAndFlush(cached);
        }
        System.out.printf("读取数据表格结束，共%d列, 数据共%d行\n", this.mergedHeader.size(), table.size());
    }

    private void afterRawData() {
        rawDataRepository.deleteIndexCol();;
        rawDataRepository.deleteEmptyData();
        rawDataRepository.updateTestName("水", "饱和度（%）水");
        rawDataRepository.updateTestName("垂直", "渗透率（mD）垂直");
        rawDataRepository.updateTestName("岩心描述", "岩性");
        rawDataRepository.updateTestName("渗透率（mD）", "渗透率（mD）水平");
        rawDataRepository.updateEmptyTestValue();
    }

    private RawData writeCellRawData(int col, int row, String value, String testDate) {
        RawData rawData = new RawData();
        String header = "";
        //读取可能，存在空白，
        if (col >= this.mergedHeader.size() || col > 50) {
            //超出边界了
            return null;
        } else {
            header = this.mergedHeader.get(col);
        }
        rawData.setTestName(header);
        rawData.setTestValue(value);
        rawData.setColIdx(col);
        rawData.setRowIdx(row);
        rawData.setReportName(this.file.getName());
        rawData.setTestDate(testDate);

        return rawData;
    }


    /**
     * 先根据报告名称删除已有数据
     * 从数据库中读取数据并整理为模板需要的行
     */
    public void saveRockAnalysisData() throws IOException {
        String fileName = this.file.getName();
        rockAnalysisDataRepository.deleteByReportName(fileName);
        final List<RawData> list = rawDataRepository.findByReportName(fileName);
        //生成排序的treeMap
        final Map<Integer, List<RawData>> rowMap = list.stream().collect(Collectors.groupingBy(RawData::getRowIdx, TreeMap::new, Collectors.toList()));
        List<RockAnalysisData> cached = new ArrayList<>();
        for(Map.Entry<Integer, List<RawData>> entry : rowMap.entrySet()) {
            List<RawData> rawDataList = entry.getValue();
            RockAnalysisData rockAnalysisData = RockAnalysisData.create(rawDataList);
            rockAnalysisData.setTestDate(this.testDate);
            rockAnalysisData.setReportName(this.file.getName());
            if (StringUtils.isBlank(rockAnalysisData.getTid())) {
                System.out.printf("检测编号为空！报告:%s%n", rockAnalysisData.getReportName());
                continue;
            }
            cached.add(rockAnalysisData);
        }
        rockAnalysisDataRepository.saveAllAndFlush(cached);
    }






    /**
     * 写入岩石分析excel模板
     */
    public List<RockAnalysisData> getRockAnalysisData(String fileName) {
        final List<RockAnalysisData> data = rockAnalysisDataRepository.findByReportName(fileName);
        data.forEach(RockAnalysisData::formatNumber);
        return data;
    }


    /**
     * 处理:岩心垂直渗透率nd
     * 转为mD
     */
    public void handleVerticalPermeability() {
        final List<RawData> list = rawDataRepository.findByTestName("岩心垂直渗透率nd");
        for (RawData rawData : list) {
            String testValue = rawData.getTestValue();
            if (StringUtils.isBlank(testValue)) {
                //空不处理
                continue;
            }
            try {
                BigDecimal bd = new BigDecimal(testValue);
                BigDecimal md = bd.divide(BigDecimal.valueOf(1000000)).divide(BigDecimal.valueOf(1000000));
                rawData.setTestValue(md.toString());
                System.out.printf("%s -> %s%n", testValue, rawData.getTestValue());
                rawDataRepository.saveAndFlush(rawData);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将n(纳米)转为m(毫米), 1m = 10^6 n
     * @param testName 检测项目名称
     */
    public void convertNdToMd(String testName) {
        final List<RawData> list = rawDataRepository.findByTestName(testName);
        for (RawData rawData : list) {
            String testValue = rawData.getTestValue();
            if (StringUtils.isBlank(testValue)) {
                //空不处理
                continue;
            }
            try {
                BigDecimal bd = new BigDecimal(testValue);
                BigDecimal md = bd.divide(BigDecimal.valueOf(1000000));
                rawData.setTestValue(md.toString());
                System.out.printf("%s -> %s%n", testValue, rawData.getTestValue());
                rawDataRepository.saveAndFlush(rawData);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理脉冲渗透率nd
     */
    public void handlePulsePermeabilityNd() {
        convertNdToMd("脉冲法超低渗透率nd");
    }




}
