package com.abt.liaohe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MinorElementExcelTest {

    @Autowired
    private MinorElementExcel minorElementExcel;

    @Test
    void readRawData() throws IOException {

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
                    minorElementExcel.setFile(path.toFile());
                    if (minorElementExcel.isMinor()) {
                        System.out.printf("read file: %s%n", fileName);
                        minorElementExcel.setFile(path.toFile());
                        minorElementExcel.handleRawData();
                    }
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void writeMinorElementData() throws IOException {
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
                    minorElementExcel.setFile(path.toFile());
                    if (minorElementExcel.isMinor()) {
                        System.out.printf("read file: %s%n", fileName);
                        minorElementExcel.saveMinorElementData();
                    }
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}