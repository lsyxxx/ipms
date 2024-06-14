package com.abt.salary.service.impl;

import com.abt.salary.SalaryExcelReadListener;
import com.abt.salary.entity.SalaryCell;
import com.abt.salary.entity.SalaryMain;
import com.abt.salary.model.SalaryPreview;
import com.abt.salary.service.SalaryService;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.repository.EmployeeRepository;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.abt.salary.Constants.*;

/**
  * 
  */
@Service
public class SalaryServiceImpl implements SalaryService {

    private EmployeeRepository employeeRepository;


    public SalaryMain createSalaryMain(MultipartFile file, String yearMonth, String group) {
        SalaryMain main = new SalaryMain();
        main.setFileName(file.getOriginalFilename());
        main.setYearMonth(yearMonth);
        main.setGroup(group);
        return main;
    }

    //抽取excel数据
    public SalaryPreview extractAndValidate(String filePath, String mainId) {
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
        if (!listener.includeName()) {
            preview.addFatalError("严重错误:表头中没有\"姓名\"，请修改工资表后重新上传");
        }
        if (!listener.includeNetPaid()) {
            preview.addFatalError("严重错误:表头中没有\"本月实发工资\"，请修改工资表后重新上传");
        }
        //严重错误直接抛出，否则后续可能存在问题
        if (!preview.hasFatalError()) {
            return preview;
        }

        preview.setJobNumberColumnIndex(listener.getJobNumberColumnIndex());
        preview.setNetPaidColumnIndex(listener.getNetPaidColumnIndex());
        preview.setNameColumnIndex(listener.getNameColumnIndex());

        //TODO: 校验：目的是保证工资条正确发放
        //1.校验员工信息
        final List<EmployeeInfo> allEmp = employeeRepository.findAllSalaryEnabled();
        checkUserInfo(allEmp, preview);

        //2. 校验“本月实发工资”列是否存在空数据
        checkNetPaidNull(preview);


        //整合数据

        return preview;
    }

    private void checkNetPaidNull(SalaryPreview preview) {
        List<List<SalaryCell>> errorList = new ArrayList<>();
        preview.getSalaryTable().forEach(row -> {
            row.forEach(cell -> {
                if (cell.getColumnIndex() == preview.getNetPaidColumnIndex()) {
                    if (StringUtils.isBlank(cell.getValue())) {
                        errorList.add(row);
                    }
                }
            });
        });
        preview.addTypedErrorAll(ERR_NETPAID_NULL, errorList);
    }

    //-- 校验人员信息
    //0. 所有人员工号是否都存在
    //1. 人员是否在employee表中
    //2. 工号和姓名是否对应（工号作为key，校验姓名）
    //3. 是否有重复人员(工号重复)
    //4. excel中是否存在离职员工
    private void checkUserInfo(List<EmployeeInfo> employees, SalaryPreview preview) {
        checkJobNumberNull(preview);
        checkUsernameNull(preview);
        checkUserContains(preview, employees);
        checkUserConsistency(preview, employees);
        checkJobNumberDuplicated(preview);
        checkUserOff(preview, employees);
    }

    //离职
    public void checkUserOff(SalaryPreview preview, List<EmployeeInfo> employees) {
        preview.getSalaryTable().forEach(row -> {
            SalaryCell jobNumCell = row.get(preview.getJobNumberColumnIndex());
            String jobNumber = jobNumCell.getValue();
            final boolean match = employees.stream().anyMatch(employee -> employee.getJobNumber().equals(jobNumber) && employee.isExit());
            if (match) {
                SalaryCell errorCell = SalaryCell.createErrorCell(jobNumCell.getRowIndex(), jobNumCell.getValue());
                row.add(errorCell);
                preview.addTypedErrorRow(ERR_USER_EXIT, row);
            }
        });
    }

    //工号重复
    public void checkJobNumberDuplicated(SalaryPreview preview) {
        Map<String, List<List<SalaryCell>>> jobNumMap = new HashMap<>();
        for (List<SalaryCell> row : preview.getSalaryTable()) {
            String jobNumber = row.get(preview.getJobNumberColumnIndex()).getValue();
            List<List<SalaryCell>> list = jobNumMap.getOrDefault(jobNumber, new ArrayList<>());
            list.add(row);
            jobNumMap.put(jobNumber, list);
        }
        jobNumMap.forEach((k, v) -> {
            //重复
            if (v.size() > 1) {
                preview.addTypedErrorAll(ERR_JOBNUM_DUPLICATED, v);
            }
        });
    }


    //工号不存在
    public void checkUserContains(SalaryPreview preview, List<EmployeeInfo> employees) {
        List<List<SalaryCell>> errorList = new ArrayList<>();
        preview.getSalaryTable().forEach(row -> {
            row.forEach(cell -> {
                if (cell.getColumnIndex() == preview.getJobNumberColumnIndex()) {
                    String jobNumber = cell.getValue();
                    final boolean exists = employees.stream().anyMatch(i -> i.getJobNumber().equals(jobNumber));
                    if (!exists) {
                        errorList.add(row);
                    }
                }
            });
        });
        preview.addTypedErrorAll(ERR_JOBNUM_NOT_EXIST, errorList);
    }

    //工号/姓名 与employee要一致
    public void checkUserConsistency(SalaryPreview preview, List<EmployeeInfo> employees) {
        for (List<SalaryCell> row : preview.getSalaryTable()) {
            String jobNumber = row.get(preview.getJobNumberColumnIndex()).getValue();
            String name = row.get(preview.getNameColumnIndex()).getValue();
            final boolean match = employees.stream().anyMatch(i -> i.getJobNumber().equals(jobNumber) && i.getName().equals(name));
            if (!match) {
                preview.addTypedErrorRow(ERR_USER_NOT_FIT, row);
            }
        }
    }

    public void checkUsernameNull(SalaryPreview preview) {
        List<List<SalaryCell>> errorList = new ArrayList<>();
        preview.getSalaryTable().forEach(row -> {
            row.forEach(cell -> {
                if (cell.getColumnIndex() == preview.getNameColumnIndex()) {
                    if (StringUtils.isBlank(cell.getValue())) {
                        errorList.add(row);
                    }
                }
            });
        });
        preview.addTypedErrorAll(ERR_USERNAME_NULL, errorList);
    }

    /**
     * 校验工号是否存在空数据
     */
    public void checkJobNumberNull(SalaryPreview preview) {
        List<List<SalaryCell>> errorList = new ArrayList<>();
        preview.getSalaryTable().forEach(row -> {
            row.forEach(cell -> {
                if (cell.getColumnIndex() == preview.getJobNumberColumnIndex()) {
                    final String value = cell.getValue();
                    if (StringUtils.isBlank(value)) {
                        errorList.add(row);
                    }
                }
            });
        });
        preview.addTypedErrorAll(ERR_JOBNUM_NULL, errorList);
    }



    public static void main(String[] args) {
        SalaryServiceImpl impl = new SalaryServiceImpl();
        impl.extractAndValidate("C:\\Users\\Administrator\\Desktop\\salary_test.xlsx", "slm1");
    }
}
