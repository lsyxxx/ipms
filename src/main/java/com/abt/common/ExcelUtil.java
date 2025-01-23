package com.abt.common;

import com.abt.sys.exception.BusinessException;
import com.alibaba.excel.metadata.data.ImageData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.aspose.cells.AutoFitterOptions;
import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 */
public class ExcelUtil {

    /**
     * 将excel文件转为pdf
     *
     * @param excelPath excel文件地址
     * @param pdfPath   转为pdf的文件地址
     * @return pdfFile
     */
    public static File excel2Pdf(String excelPath, String pdfPath) throws Exception {
        if (!Files.isRegularFile(Paths.get(excelPath))) {
            throw new BusinessException("非法的路径: " + excelPath);
        }
        AutoFitterOptions options = new AutoFitterOptions();
        options.setOnlyAuto(true);
        Workbook wb = new Workbook(excelPath);
        wb.getWorksheets().get(0).autoFitRows(options);
        File pdfFile = new File(pdfPath);
        FileOutputStream fos = new FileOutputStream(pdfFile);
        PdfSaveOptions saveOptions = new PdfSaveOptions();
        saveOptions.setAllColumnsInOnePagePerSheet(true);
        wb.save(fos, saveOptions);
        return pdfFile;
    }

    /**
     * easyexcel 写入图片对象，默认margin 10
     * @param imageFile 图片文件
     * @return WriteCellData<Void
     */
    public static WriteCellData<Void> createImageData(File imageFile) throws IOException {
        WriteCellData<Void> writeCellData = new WriteCellData<>();
        ImageData imageData = new ImageData();
        imageData.setImage(Files.readAllBytes(imageFile.toPath()));
        imageData.setImageType(ImageData.ImageType.PICTURE_TYPE_PNG);
        imageData.setTop(10);
        imageData.setBottom(10);
        imageData.setLeft(10);
        imageData.setRight(10);
        writeCellData.setImageDataList(List.of(imageData));
        return writeCellData;
    }

    public static WriteCellData<Void> createImageDataWithMargin2(File imageFile) throws IOException {
        WriteCellData<Void> writeCellData = new WriteCellData<>();
        ImageData imageData = new ImageData();
        imageData.setImage(Files.readAllBytes(imageFile.toPath()));
        imageData.setImageType(ImageData.ImageType.PICTURE_TYPE_PNG);
        imageData.setTop(2);
        imageData.setBottom(2);
        imageData.setLeft(2);
        imageData.setRight(2);
        writeCellData.setImageDataList(List.of(imageData));
        return writeCellData;
    }

}
