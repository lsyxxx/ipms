package com.abt.common;

import com.abt.sys.exception.BusinessException;
import com.aspose.cells.AutoFitterOptions;
import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

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
}
