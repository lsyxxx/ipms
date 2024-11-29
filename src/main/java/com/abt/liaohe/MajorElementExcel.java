package com.abt.liaohe;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.abt.liaohe.Util.*;

/**
 *
 */
@Service
public class MajorElementExcel {

    private final RawDataRepository rawDataRepository;
    private final MajorElementRepository majorElementRepository;


    @Getter
    @Setter
    private File file;
    private Workbook workbook;
    private Sheet sheet;
    @Getter
    @Setter
    private int headerRow = 2;

    /**
     * 数据起始行
     */
    private int startRow = 3;
    //默认21，可能会少
    private int headerLen = 21;

    private String testDate;

    private ArrayList<String> header = new ArrayList<>();

    public MajorElementExcel(RawDataRepository rawDataRepository, MajorElementRepository majorElementRepository) {
        this.rawDataRepository = rawDataRepository;
        this.majorElementRepository = majorElementRepository;
        header = new ArrayList<>();
    }

    private void excelReader(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            workbook = WorkbookFactory.create(fis);
            sheet = workbook.getSheetAt(0);
        }
    }

    public void readExcel() throws IOException {
        excelReader(this.file);
        readHeader();
        rawDataRepository.deleteByReportName(this.file.getName());
        readData();
        afterReadExcel();
    }

    private void afterReadExcel() {
//        rawDataRepository.deleteIndexCol();;
        rawDataRepository.deleteEmptyData();
//        rawDataRepository.updateTestName("水分（%）", "水分%空气干燥基");
//        rawDataRepository.updateTestName("固定碳（%）", "固定碳%空气干燥基 ");
//        rawDataRepository.updateTestName("灰分（%）", "灰分%空气干燥基");
//        rawDataRepository.updateTestName("挥发分（%）", "挥发分 %空气干燥基");
//        rawDataRepository.updateTestName("岩性入井定名", "岩性");
        rawDataRepository.updateEmptyTestValue();
    }

    /**
     * 获取表头
     * 固定row=2
     */
    private void readHeader() {
        sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(this.headerRow);
        this.headerLen = headerRow.getLastCellNum();
        headerRow.forEach(cell -> {
            String name = cell.getStringCellValue();
            name = Util.removeBlank(name);
            header.add(name);
        });
        System.out.println(header);
        //简单验证表头是否正确
        if (!header.contains("序号")) {
            System.out.println("表头不正确!");
        }
    }

    /**
     * 读取excel数据
     */
    private void readData() {
        for (Row row : sheet) {
            if (row.getRowNum() < this.startRow) {
                continue;
            }
            List<RawData> cached = new ArrayList<>();
            final boolean isEmpty = Util.validateEmptyRow(row);
            if (isEmpty) {
                continue;
            }

            for (Cell cell : row) {
                if (cell.getColumnIndex() > 50) {
                    //设个边界
                    break;
                }

                if (cell.getColumnIndex() > header.size()) {
                    continue;
                }
                String cellValue = getCellValueAsString(cell);
                cellValue = removeBlank(cellValue);
                System.out.printf("列: %d, value: %s\n", cell.getColumnIndex(), cellValue);
                RawData rawData = new RawData();
                rawData.setTestValue(cellValue);
                rawData.setTestName(header.get(cell.getColumnIndex()));
                rawData.setColIdx(cell.getColumnIndex());
                rawData.setRowIdx(cell.getRowIndex());
                rawData.setReportName(this.file.getName());
                cached.add(rawData);
            }
            //保存数据库
            rawDataRepository.saveAllAndFlush(cached);
        }
    }

    public void saveMajorElementDB() {
        //排除有问题的
        if (file.getName().contains("JC2021094B")) {
            return;
        }
        final List<RawData> rawData = rawDataRepository.findByReportName(this.file.getName());
        final Map<Integer, List<RawData>> rowMap = rawData.stream().collect(Collectors.groupingBy(RawData::getRowIdx, TreeMap::new, Collectors.toList()));
        List<MajorElement> cached = new ArrayList<>();
        for(Map.Entry<Integer, List<RawData>> entry : rowMap.entrySet()) {
            List<RawData> row = entry.getValue();
            MajorElement majorElement = new MajorElement(row);
            majorElement.setReportName(this.file.getName());
            cached.add(majorElement);
        }
        majorElementRepository.saveAllAndFlush(cached);
    }

    public static void main(String[] args) {
        String dirPath = "F:\\00平台资料汇总\\辽河数据\\主微量稀土报告\\主微量稀土报告\\";
        Path dir = Paths.get(dirPath);
        if (!Files.isDirectory(dir)) {
            throw new RuntimeException(dirPath + " is not a directory");
        }
        try(DirectoryStream<Path> paths = Files.newDirectoryStream(dir)) {
            int i = 0;
            for (Path path : paths) {
                if (i == 3) {
                    return;
                }
                if (Files.isRegularFile(path)) {
                    String fileName = path.getFileName().toString();
                    if (fileName.contains("主量") || fileName.contains("常量")) {
                        System.out.printf("read file: %s%n", fileName);
//                        MajorElementExcel excel = new MajorElementExcel(null, majorElementRepository);
//                        excel.setFile(path.toFile());
//                        excel.readExcel();
                    }
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
