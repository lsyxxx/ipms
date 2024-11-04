package com.abt.liaohe;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据导出
 * 1. 读取excel，将所有列都导出，合并后的表头作为检测项目名称
 * 2. 导入数据库中
 * 3. 导入数据后清理数据，处理表头
 * 4. 将清理后的数据导入excel
 */
@Service
public class DataExport {

    private final RawDataRepository rawDataRepository;
    private final RockAnalysisDataRepository rockAnalysisDataRepository;

    public DataExport(RawDataRepository rawDataRepository, RockAnalysisDataRepository rockAnalysisDataRepository) {
        this.rawDataRepository = rawDataRepository;
        this.rockAnalysisDataRepository = rockAnalysisDataRepository;
    }

    public static void main(String[] args) throws IOException {
//        DataExport dataExport = new DataExport();
//        dataExport.export();
    }


    public void handleData() {
        RockAnalysisExcel excel = new RockAnalysisExcel(rawDataRepository, rockAnalysisDataRepository);
//        excel.handleVerticalPermeability();
        excel.handlePulsePermeabilityNd();
    }


    /**
     * 读取数据
     * 一个excel读取一次(或最多500条)，写入数据库，写入excel
     * 数据为xls格式
     */
    public void readRawData(String dirPath) throws IOException {
        Path dir = Paths.get(dirPath);
        if (!Files.isDirectory(dir)) {
            throw new RuntimeException(dirPath + " is not a directory");
        }
        try(DirectoryStream<Path> paths = Files.newDirectoryStream(dir)) {
            for (Path path : paths) {
                if (Files.isRegularFile(path)) {
                    String fileName = path.getFileName().toString();
                    System.out.printf("read file: %s%n", fileName);
                    if (fileName.contains("物性")) {
                        RockAnalysisExcel rockAnalysisExcel = new RockAnalysisExcel(rawDataRepository, rockAnalysisDataRepository);
                        rockAnalysisExcel.setFile(path.toFile());
                        rockAnalysisExcel.extractToDb();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入rockAnalysisData数据库
     * @param dirPath
     * @throws IOException
     */
    public void writeRockAnalysisData(String dirPath) throws IOException {
        Path dir = Paths.get(dirPath);
        if (!Files.isDirectory(dir)) {
            throw new RuntimeException(dirPath + " is not a directory");
        }
        try(DirectoryStream<Path> paths = Files.newDirectoryStream(dir)) {
            for (Path path : paths) {
                if (Files.isRegularFile(path)) {
                    String fileName = path.getFileName().toString();
                    System.out.printf("read file: %s%n", fileName);
                    if (fileName.contains("物性")) {
                        RockAnalysisExcel rockAnalysisExcel = new RockAnalysisExcel(rawDataRepository, rockAnalysisDataRepository);
                        rockAnalysisExcel.setFile(path.toFile());
                        rockAnalysisExcel.saveRockAnalysisData();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void writeExcel(String dirPath, String newFile) {
        Path dir = Paths.get(dirPath);
        if (!Files.isDirectory(dir)) {
            throw new RuntimeException(dirPath + " is not a directory");
        }

        String template = "F:\\00平台资料汇总\\辽河数据\\辽河2024\\rock_analysis_template.xlsx";
        try(DirectoryStream<Path> paths = Files.newDirectoryStream(dir)) {
            List<RockAnalysisData> all = new ArrayList<>();
            for (Path path : paths) {
                if (Files.isRegularFile(path)) {
                    System.out.printf("read file: %s%n", path.getFileName().toString());
                    RockAnalysisExcel rockAnalysisExcel = new RockAnalysisExcel(rawDataRepository, rockAnalysisDataRepository);
                    //写excel
                    final List<RockAnalysisData> data = rockAnalysisExcel.getRockAnalysisData(path.toFile().getName());
                    all.addAll(data);
                }
            }
            //排序
            all.stream().sorted(Comparator.comparing(RockAnalysisData::getTid)).toList();
            System.out.printf("写入所有数据%d条%n", all.size());
            writeExcel(newFile, template, all);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void writeExcel(String fileName, String templateFileName, List<RockAnalysisData> data) {
        try(ExcelWriter excelWriter = EasyExcel.write(fileName).withTemplate(templateFileName).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            excelWriter.fill(data, writeSheet);
        }
    }


}
