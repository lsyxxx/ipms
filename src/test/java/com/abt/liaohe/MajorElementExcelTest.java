package com.abt.liaohe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
class MajorElementExcelTest {

    @Autowired
    private RawDataRepository rawDataRepository;
    @Autowired
    private MajorElementRepository majorElementRepository;


    @Test
    void readHeader() {
    }

    @Test
    void readData() {
        String dirPath = "F:\\00平台资料汇总\\辽河数据\\主微量稀土报告\\主微量稀土报告\\";
        Path dir = Paths.get(dirPath);
        if (!Files.isDirectory(dir)) {
            throw new RuntimeException(dirPath + " is not a directory");
        }
        try(DirectoryStream<Path> paths = Files.newDirectoryStream(dir)) {
            int i = 0;
            for (Path path : paths) {
                if (Files.isRegularFile(path)) {
                    String fileName = path.getFileName().toString();
                    System.out.printf("read file: %s%n", fileName);
                    if (fileName.contains("主量") || fileName.contains("常量")) {
                        MajorElementExcel excel = new MajorElementExcel(rawDataRepository, majorElementRepository);
                        excel.setFile(path.toFile());
                        excel.saveMajorElementDB();
                    }
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}