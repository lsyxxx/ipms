package com.abt.liaohe;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

/**
 *
 */
@RequiredArgsConstructor
public abstract class AbstractExcelHandler {
    private String dataRoot = "F:\\00平台资料汇总\\辽河数据\\辽河油田历年检测报告-截止2024.11.26\\";

    @NonNull
    private RawDataRepository rawDataRepository;
    @NonNull
    private TestBaseTableRepository testBaseTableRepository;

    @Getter
    @Setter
    private File file;
    @Getter
    private Workbook workbook;
    @Getter
    private Sheet sheet;

    @Getter
    @Setter
    private List<String> header;

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
        readAndSaveRawData();
        afterSaveRawData();
    }
    public boolean validateCell(Cell cell, int headerSize) {
        return cell.getColumnIndex() < 50 && cell.getColumnIndex() <= headerSize;
    }

    public void copyFiles(String path, String copyDir) throws IOException {
        if (StringUtils.isBlank(path)) {
            path = dataRoot;
        }
        if (StringUtils.isBlank(copyDir)) {
            throw new RuntimeException("copyDir is empty");
        }

        Path dir = Paths.get(path);
        if (!Files.isDirectory(dir)) {
            throw new RuntimeException(path + " is not a directory");
        }
        Files.walkFileTree(dir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
                // 检查文件扩展名
                String fileName = filePath.getFileName().toString();
                if ((fileName.toLowerCase().endsWith(".xls") || fileName.toLowerCase().endsWith(".xlsx")) && supportFile(filePath.toFile())) {
                    //将文件copy到copyDir
                    System.out.println(fileName);
                    try {
                        Files.copy(filePath, Paths.get(copyDir + fileName), StandardCopyOption.REPLACE_EXISTING);
                    } catch (FileAlreadyExistsException e) {
                        System.out.println(e.getMessage());
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void walkFiles(String dir, ExcelHandler handler) throws IOException {
        if (StringUtils.isBlank(dir)) {
            throw new RuntimeException("path is empty");
        }
        Path pDir = Paths.get(dir);
        if (!Files.isDirectory(pDir)) {
            throw new RuntimeException(dir + " is not a directory");
        }
        Files.walkFileTree(pDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path pFile, BasicFileAttributes attrs) throws IOException {
                // 检查文件扩展名
                if (supportFile(pFile.toFile())) {
                    handler.handle();
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 读取表头
     */
    abstract void readHeader();

    /**
     * 读取excel数据并保存到rawData中
     */
    abstract void readAndSaveRawData();

    /**
     * 整个excel保存rawData后处理。
     */
    abstract void afterSaveRawData();

    abstract boolean supportFile(File file);



}
