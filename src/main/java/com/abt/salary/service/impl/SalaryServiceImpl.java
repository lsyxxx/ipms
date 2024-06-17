package com.abt.salary.service.impl;

import com.abt.common.util.FileUtil;
import com.abt.salary.SalaryExcelReadListener;
import com.abt.salary.entity.SalaryCell;
import com.abt.salary.entity.SalaryMain;
import com.abt.salary.entity.SalarySlip;
import com.abt.salary.model.SalaryPreview;
import com.abt.salary.repository.SalaryCellRepository;
import com.abt.salary.repository.SalaryMainRepository;
import com.abt.salary.repository.SalarySlipRepository;
import com.abt.salary.service.SalaryService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.repository.EmployeeRepository;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.*;

import static com.abt.salary.Constants.*;

/**
  * 
  */
@Service
@Slf4j
public class SalaryServiceImpl implements SalaryService {

    private final EmployeeRepository employeeRepository;
    private final SalaryMainRepository salaryMainRepository;
    private final SalaryCellRepository salaryCellRepository;
    private final SalarySlipRepository salarySlipRepository;

    @Value("${com.abt.file.upload.save}")
    private String fileRoot;

    public SalaryServiceImpl(EmployeeRepository employeeRepository, SalaryMainRepository salaryMainRepository, SalaryCellRepository salaryCellRepository, SalarySlipRepository salarySlipRepository) {
        this.employeeRepository = employeeRepository;
        this.salaryMainRepository = salaryMainRepository;
        this.salaryCellRepository = salaryCellRepository;
        this.salarySlipRepository = salarySlipRepository;
    }

    @Override
    public SalaryMain createSalaryMain(MultipartFile file, String yearMonth, String group, Integer sheetNo) {
        SalaryMain main = new SalaryMain();
        main.setFileName(file.getOriginalFilename());
        main.setYearMonth(yearMonth);
        main.setGroup(group);
        main.setSheetNo(sheetNo);
        return main;
    }

    //抽取excel数据
    @Override
    public SalaryPreview extractAndValidate(InputStream fis, SalaryMain salaryMain) {
        final SalaryExcelReadListener listener = new SalaryExcelReadListener(salaryMain.getId());
        EasyExcel.read(fis, listener)
                .excelType(ExcelTypeEnum.XLSX)
                .headRowNumber(SalaryExcelReadListener.DATA_START_IDX)
                //sheetNo从0开始
                .extraRead(CellExtraTypeEnum.MERGE).sheet(salaryMain.getSheetNo()).doRead();
        salaryMain.setNetPaidColumnIndex(listener.getNetPaidColumnIndex());
        salaryMain.setJobNumberColumnIndex(listener.getJobNumberColumnIndex());
        salaryMain.setName(listener.getNameColumnIndex());

        final int jobNumberColumnIndex = listener.getJobNumberColumnIndex();
        final int netPaidColumnIndex = listener.getNetPaidColumnIndex();
        log.info("=== jobNumberColumnIndex {} =======", jobNumberColumnIndex);
        log.info("=== netPaidColumnIndex {} =======", netPaidColumnIndex);

        //组装preview
        SalaryPreview preview = this.buildPreview(listener, salaryMain);
        preview.setSalaryMain(salaryMain);

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
            log.info("==== 存在严重错误! {}",  preview.getFatalError());
            return preview;
        }

        preview.setJobNumberColumnIndex(listener.getJobNumberColumnIndex());
        preview.setNetPaidColumnIndex(listener.getNetPaidColumnIndex());
        preview.setNameColumnIndex(listener.getNameColumnIndex());

        //-- 校验人员信息:目的是保证工资条正确发放
        //0. 所有人员工号是否都存在
        //1. 人员是否在employee表中
        //2. 工号和姓名是否对应（工号作为key，校验姓名）
        //3. 是否有重复人员(工号重复)
        //4. excel中是否存在离职员工
        final List<EmployeeInfo> employees = employeeRepository.findAll();
        checkJobNumberNull(preview);
        checkUsernameNull(preview);
        checkUserContains(preview, employees);
        checkUserConsistency(preview, employees);
        checkJobNumberDuplicated(preview);
        checkUserEnabled(preview, employees);

        //-- 校验“本月实发工资”列是否存在空数据
        checkNetPaidNull(preview);

        //整合数据
        preview.setSlipTable(this.buildSlipTable(preview.getRawTable()));
        log.info("===== 抽取数据及校验完成=======");
        return preview;
    }

    private SalaryPreview buildPreview(SalaryExcelReadListener listener, SalaryMain main) {
        SalaryPreview preview = new SalaryPreview();
        preview.setYearMonth(main.getYearMonth());
        preview.setGroup(main.getGroup());
        preview.setFilePath(main.getFilePath());
        preview.setSalaryMain(main);
        if (listener == null) {
            return preview;
        }
        preview.setRawTable(listener.getTableList());
        preview.setHeaderMap(listener.getMergedHeader());
        preview.setNameColumnIndex(listener.getNameColumnIndex());
        preview.setNetPaidColumnIndex(listener.getNetPaidColumnIndex());
        preview.setJobNumberColumnIndex(listener.getJobNumberColumnIndex());
        preview.buildHeader();
        return preview;
    }

    private void checkNetPaidNull(SalaryPreview preview) {
        preview.getRawTable().forEach(row -> {
            row.forEach(cell -> {
                if (cell.getColumnIndex() == preview.getNetPaidColumnIndex()) {
                    if (StringUtils.isBlank(cell.getValue())) {
                        setRowErrorFlag(row);
                        preview.addTypedErrorRow(ERR_NETPAID_NULL, row);
                    }
                }
            });
        });
    }

    //离职/未启用工资选项
    public void checkUserEnabled(SalaryPreview preview, List<EmployeeInfo> employees) {
        preview.getRawTable().forEach(row -> {
            SalaryCell jobNumCell = row.get(preview.getJobNumberColumnIndex());
            String jobNumber = jobNumCell.getValue();
//            final boolean match = employees.stream().anyMatch(employee -> employee.getJobNumber().equals(jobNumber) && employee.isExit());
            final Optional<EmployeeInfo> emp = employees.stream().filter(i -> i.getJobNumber().equals(jobNumber) && (i.isExit() || !i.salaryIsEnabled())).findAny();
            if (emp.isPresent()) {
                System.out.println("离职/未启用员工: " + jobNumber);
                setRowErrorFlag(row);
                preview.addTypedErrorRow(ERR_USER_EXIT, row);
            }

        });
    }

    //工号重复
    public void checkJobNumberDuplicated(SalaryPreview preview) {
        Map<String, List<List<SalaryCell>>> jobNumMap = new HashMap<>();
        for (List<SalaryCell> row : preview.getRawTable()) {
            String jobNumber = row.get(preview.getJobNumberColumnIndex()).getValue();
            List<List<SalaryCell>> list = jobNumMap.getOrDefault(jobNumber, new ArrayList<>());
            list.add(row);
            jobNumMap.put(jobNumber, list);
        }
        jobNumMap.forEach((k, v) -> {
            //重复
            if (v.size() > 1) {
                v.forEach(this::setRowErrorFlag);
                preview.addTypedErrorAll(ERR_JOBNUM_DUPLICATED, v);
            }
        });
    }


    //工号不存在
    public void checkUserContains(SalaryPreview preview, List<EmployeeInfo> employees) {
        List<List<SalaryCell>> errorList = new ArrayList<>();
        preview.getRawTable().forEach(row -> {
             String jobNumber = row.get(0).getJobNumber();
            final boolean exists = employees.stream().anyMatch(i -> i.getJobNumber().equals(jobNumber));
            if (!exists) {
                setRowErrorFlag(row);
                errorList.add(row);
            }
        });
        preview.addTypedErrorAll(ERR_JOBNUM_NOT_EXIST, errorList);
    }

    //工号/姓名/工资组 与employee要一致
    public void checkUserConsistency(SalaryPreview preview, List<EmployeeInfo> employees) {
        for (List<SalaryCell> row : preview.getRawTable()) {
            String jobNumber = row.get(preview.getJobNumberColumnIndex()).getValue();
            String name = row.get(preview.getNameColumnIndex()).getValue();
            final boolean match = employees.stream().anyMatch(i -> i.getJobNumber().equals(jobNumber) && i.getName().equals(name) && i.getCompany().equals(preview.getGroup()));
            if (!match) {
                setRowErrorFlag(row);
                preview.addTypedErrorRow(ERR_USER_NOT_FIT, row);
            }
        }
    }

    public void checkUsernameNull(SalaryPreview preview) {
        List<List<SalaryCell>> errorList = new ArrayList<>();
        preview.getRawTable().forEach(row -> {
            row.forEach(cell -> {
                if (cell.getColumnIndex() == preview.getNameColumnIndex()) {
                    if (StringUtils.isBlank(cell.getValue())) {
                        setRowErrorFlag(row);
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
        preview.getRawTable().forEach(row -> {
            SalaryCell cell = row.get(0);
            if (StringUtils.isBlank(cell.getJobNumber())) {
                setRowErrorFlag(row);
                errorList.add(row);
            }
        });
        preview.addTypedErrorAll(ERR_JOBNUM_NULL, errorList);
    }

    private SalaryCell setRowErrorFlag(List<SalaryCell> row) {
        final SalaryCell rowInfoCell = getRowInfoCell(row);
        rowInfoCell.doError();
        return rowInfoCell;
    }

    private SalaryCell getRowInfoCell(List<SalaryCell> row) {
        return row.get(SL_ROW_INFO_IDX);
    }

    /**
     * 生成可以发工资条的table
     * @param rawTable 原始数据
     */
    private List<List<SalaryCell>> buildSlipTable(List<List<SalaryCell>> rawTable) {
        List<List<SalaryCell>> slipTable = new ArrayList<>();
        rawTable.forEach(row -> {
            List<SalaryCell> newRow = new ArrayList<>(row);
            SalaryCell infoCell = getRowInfoCell(newRow);
            if (!infoCell.isRowError()) {
                newRow.remove(SL_ROW_INFO_IDX);
                slipTable.add(newRow);
            }
        });
        return slipTable;
    }

    private String createFilePath(String yearMonth) {
        return fileRoot + File.separator + SERVICE + File.separator + yearMonth + File.separator;
    }


    @Override
    public void saveExcelFile(MultipartFile file, SalaryMain main) {
        String filePath = createFilePath(main.getYearMonth());
        final String newName = FileUtil.saveFile(file, filePath, true);
        String fullPath = filePath + newName;
        main.setFilePath(fullPath);
        main.setFileName(file.getOriginalFilename());
    }

    //导入数据库和发送工资条到个人
    @Transactional
    @Override
    public void salaryImport(SalaryMain main, SalaryPreview preview) {
        //生成工资条
        List<SalarySlip> slips = new ArrayList<>();
        for (List<SalaryCell> row : preview.getSlipTable()) {
            final String jobNumber = row.get(SL_ROW_INFO_IDX).getJobNumber();
            final String rowId = row.get(SL_ROW_INFO_IDX).getRowId();
            SalarySlip slip = SalarySlip.create(main, jobNumber, rowId);
            slip.send();
            slips.add(slip);
        }
        //保存
        salaryMainRepository.save(main);
        preview.getSlipTable().forEach(salaryCellRepository::saveAll);
        salarySlipRepository.saveAll(slips);
    }

    //根据 工资年月/工资组查看导入/发送工资条记录
    @Override
    public List<SalaryMain> findImportRecordBy(String yearMonth, String group) {
        final List<SalaryMain> list = salaryMainRepository.findByYearMonthAndGroupNullable(yearMonth, group);
        //统计数据

        return null;

    }

    //根据salaryMain.id查询发送情况
    @Override
    public List<SalarySlip> findSendSlips(String mid) {
        return salarySlipRepository.findByMainIdOrderByJobNumberAsc(mid);
    }

    @Transactional
    @Override
    public void deleteSalaryRecord(String mid) {
        try {
            salaryCellRepository.deleteByMid(mid);
            salarySlipRepository.deleteByMainId(mid);
            salaryCellRepository.deleteByMid(mid);
        } catch (Exception e) {
            log.error("删除工资条记录失败!", e);
            throw new BusinessException("删除工资条记录失败!");
        }
    }

    @Override
    public List<String> getSalaryGroup() {
        return employeeRepository.findDistinctGroup();
    }



}
