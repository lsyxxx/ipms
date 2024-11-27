package com.abt.liaohe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CoalIndustryAnalysisExcelTest {

    @Autowired
    private CoalIndustryAnalysisExcel coalIndustryAnalysisExcel;

    @Test
    void readRawData() {
        String dirPath = "F:\\00平台资料汇总\\辽河数据\\辽河2024\\煤工业分析-all";
        Path dir = Paths.get(dirPath);
        if (!Files.isDirectory(dir)) {
            throw new RuntimeException(dirPath + " is not a directory");
        }
        try(DirectoryStream<Path> paths = Files.newDirectoryStream(dir)) {
            for (Path path : paths) {
                if (Files.isRegularFile(path)) {
                    String fileName = path.getFileName().toString();
                    System.out.printf("read file: %s%n", fileName);
                    if (fileName.contains("工业")) {
                        coalIndustryAnalysisExcel.readRawData(fileName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}