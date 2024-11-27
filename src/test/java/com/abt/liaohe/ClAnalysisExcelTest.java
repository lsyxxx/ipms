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
class ClAnalysisExcelTest {

    @Autowired
    private ClAnalysisDataRepository clAnalysisDataRepository;

    @Autowired
    private RawDataRepository rawDataRepository;

    @Test
    void testSave() {
        String dirPath = "F:\\00平台资料汇总\\辽河数据\\辽河2024\\2024-物性";
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
                        ClAnalysisExcel clAnalysisExcel = new ClAnalysisExcel(clAnalysisDataRepository, rawDataRepository, path.toFile());
                        clAnalysisExcel.saveClData();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void export() {
        ClAnalysisExcel clAnalysisExcel = new ClAnalysisExcel(clAnalysisDataRepository, rawDataRepository, null);
        clAnalysisExcel.exportExcel();
    }
}