package com.abt.salary.service.impl;

import com.abt.salary.SalaryExcelReadListener;
import com.abt.salary.entity.SalaryCell;
import com.abt.salary.entity.SalaryMain;
import com.abt.salary.model.SalaryPreview;
import com.abt.salary.service.SalaryService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
  * 
  */
@Service
public class SalaryServiceImpl implements SalaryService {



    public SalaryMain createSalaryMain(MultipartFile file, String yearMonth, String group) {
        SalaryMain main = new SalaryMain();
        main.setFileName(file.getOriginalFilename());
        main.setYearMonth(yearMonth);
        main.setGroup(group);
        return main;
    }

    //抽取excel数据
    public SalaryPreview extractData(String filePath, String mainId) {
        SalaryPreview preview = new SalaryPreview();
        final SalaryExcelReadListener listener = new SalaryExcelReadListener(mainId);
        EasyExcel.read(new File(filePath), listener)
                .excelType(ExcelTypeEnum.XLSX)
                .headRowNumber(SalaryExcelReadListener.DATA_START_IDX)
                //sheetNo从0开始
                .extraRead(CellExtraTypeEnum.MERGE).sheet(0).doRead();
        final List<List<SalaryCell>> tableList = listener.getTableList();
        final Map<Integer, String> mergedHeader = listener.getMergedHeader();
        final int jobNumberColumnIndex = listener.getJobNumberColumnIndex();
        final int netPaidColumnIndex = listener.getNetPaidColumnIndex();
        System.out.println("=== Header ======");
        System.out.println(mergedHeader.toString());
        System.out.println("=== Table =======");
        tableList.forEach(row -> {
            System.out.println("|Row" + row.get(0).getRowIndex());
            row.forEach(cell -> {
                System.out.println("--|Cell " + cell.getColumnIndex() + ": " + cell.getColumnName() + " - " + cell.getValue());
            });
        });
        System.out.printf("=== jobNumberColumnIndex %d =======\n", jobNumberColumnIndex);
        System.out.printf("=== netPaidColumnIndex %d =======\n", netPaidColumnIndex);

        //校验
        if (!listener.includeJobNumber()) {
            preview.addFatalError("严重错误:表头中没有\"工号\"，请修改工资表后重新上传");
        }
        if (!listener.includeNetPaid()) {
            preview.addFatalError("严重错误:表头中没有\"本月实发工资\"，请修改工资表后重新上传");
        }

        //校验人员



        return preview;
    }



    public static void main(String[] args) {
        SalaryServiceImpl impl = new SalaryServiceImpl();
        impl.extractData("C:\\Users\\Administrator\\Desktop\\salary_test.xlsx", "slm1");
    }
}
