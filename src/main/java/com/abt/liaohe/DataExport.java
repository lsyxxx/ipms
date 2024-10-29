package com.abt.liaohe;


import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 数据导出
 * 1. 读取excel
 * 2. 导入数据库中
 * 3. 从数据库导入excel
 */
public class DataExport {

    public static void main(String[] args) throws IOException {
        DataExport dataExport = new DataExport();
        dataExport.export();
    }
    public void export() throws IOException {
        String dir = "F:\\00平台资料汇总\\辽河数据\\辽河2024\\2024-物性\\";
        readData(dir);
//        importDb();
//        writeExcel();

    }

    private void readExcelOne(String fileName) {


    }

    /**
     * 读取数据
     * 一个excel读取一次(或最多500条)，写入数据库，写入excel
     * 数据为xls格式
     */
    public void readData(String dirPath) throws IOException {
        Path dir = Paths.get(dirPath);
        if (!Files.isDirectory(dir)) {
            throw new RuntimeException(dirPath + " is not a directory");
        }
        try(DirectoryStream<Path> paths = Files.newDirectoryStream(dir)) {
            for (Path path : paths) {
                if (Files.isRegularFile(path)) {
                    RockAnalysisExcel rae = new RockAnalysisExcel();
                    System.out.printf("read file: %s\n", path.getFileName().toString());
                    rae.setFile(path.toFile());
                    rae.read();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 写入excel
     */
    public void writeExcel() {

    }

    /**
     * 导入数据到数据库
     */
    public void importDb() {
        //新建一个临时表用于保存数据

    }


}
