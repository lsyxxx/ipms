package com.abt.salary.service.impl;

import com.abt.common.model.ValidationResult;
import com.abt.common.util.ValidateUtil;
import com.abt.salary.entity.SalaryDetail;
import com.abt.salary.entity.SalaryMain;
import com.abt.salary.entity.SalarySlip;
import com.abt.salary.model.SalaryMainDTO;
import com.abt.salary.model.SalaryPreview;
import com.abt.salary.model.SalarySlipBoard;
import com.abt.salary.repository.SalaryDetailRepository;
import com.abt.salary.repository.SalaryMainRepository;
import com.abt.salary.repository.SalarySlipRepository;
import com.abt.salary.service.SalaryExcelReadListener;
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
import org.springframework.web.servlet.View;

import java.time.format.DateTimeFormatter;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
public class SalaryServiceImpl implements SalaryService {

    private final SalaryMainRepository salaryMainRepository;
    private final SalaryDetailRepository salaryDetailRepository;
    private final EmployeeRepository employeeRepository;
    private final SalarySlipRepository salarySlipRepository;
    /**
     * 表头行数
     */
    public static final int HEADER_ROW_NUM = 3;

    @Value("${sl.title.format}")
    private String salaryTitleFormat;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");


    public SalaryServiceImpl(SalaryDetailRepository salaryDetailRepository, View error, SalaryMainRepository salaryMainRepository, EmployeeRepository employeeRepository, SalarySlipRepository salarySlipRepository) {
        this.salaryDetailRepository = salaryDetailRepository;
        this.salaryMainRepository = salaryMainRepository;
        this.employeeRepository = employeeRepository;
        this.salarySlipRepository = salarySlipRepository;
    }

    @Override
    public SalaryMain createSalaryMain(String yearMonth, String group, String netPaidColumnName) {
        //validate
        ValidateUtil.ensurePropertyNotnull(yearMonth, "yearMonth(必须选择发放工资年月)");
        ValidateUtil.ensurePropertyNotnull(group, "group(必须选择工资组)");

        //create
        //应该前端传入SalaryMain对象
        SalaryMain slm = new SalaryMain();
        slm.setYearMonth(yearMonth);
        slm.setGroup(group);
        slm.setNetPaidColumnName(netPaidColumnName);
        slm.setTitle(MessageFormat.format(salaryTitleFormat, group, yearMonth));
        final ValidationResult result = ValidateUtil.validateEntity(slm);
        if (!result.isPass()) {
            //校验失败
            throw new BusinessException(result.getDescription() + " " + result.getParameters().toString());
        }
        return salaryMainRepository.save(slm);
    }

    @Override
    public SalaryPreview previewSalaryDetail(InputStream inputStream, String sheetName, SalaryMain salaryMain) {
        SalaryPreview salaryPreview = SalaryPreview.create();
//        Map<String, Map<SalaryDetail, ValidationResult>> typedErrorMap = new HashMap<>();
        //读取excel
        SalaryExcelReadListener listener = new SalaryExcelReadListener(this, salaryMain.getId());
        EasyExcel.read(inputStream, SalaryDetail.class, listener)
                // 需要读取批注 默认不读取
//                .extraRead(CellExtraTypeEnum.COMMENT)
                // 需要读取超链接 默认不读取
//                .extraRead(CellExtraTypeEnum.HYPERLINK)
                // 需要读取合并单元格信息 默认不读取
                .excelType(ExcelTypeEnum.XLSX)
                .headRowNumber(HEADER_ROW_NUM)
                .extraRead(CellExtraTypeEnum.MERGE).sheet(sheetName).doRead();
        List<SalaryDetail> tempSalary = listener.getTempSalaryDetails();
        salaryPreview.setSalaryDetails(tempSalary);
        final Map<SalaryDetail, ValidationResult> errorDetailMap = listener.getErrorDetailMap();
        final Map<Integer, Map<Integer, String>> headMap = listener.getHeadMap();
//        final Map<Integer, String> header = createHeader(headMap);
        if (errorDetailMap != null) {
            salaryPreview.addErrorMap("异常数据", errorDetailMap);
            salaryMain.setState(SalaryMain.STATE_ERROR);
        } else {
            salaryMain.setState(SalaryMain.STATE_SUCCESS);
        }
        final List<EmployeeInfo> emp = employeeRepository.findAll();
        //其他校验
        final Map<SalaryDetail, ValidationResult> employNotExistsError = employNotExistsError(salaryMain, tempSalary, emp);
        salaryPreview.addErrorMap("不在人员信息表中", employNotExistsError);
        final Map<SalaryDetail, ValidationResult> duplicatedNameError = duplicatedJobNumberError(salaryMain, tempSalary);
        salaryPreview.addErrorMap("工号重复", duplicatedNameError);
        salaryMainRepository.save(salaryMain);
        final Map<SalaryDetail, ValidationResult> quitError = employeeQuitError(salaryMain, tempSalary, emp);
        salaryPreview.addErrorMap("已离职", quitError);
        return salaryPreview;
    }

    /**
     * 工资表异常校验: excel中人员不在人员信息中
     * @param salaryMain 主体
     * @param tempSalary 数据
     * @return Map<SalaryDetail, ValidationResult>
     */
    public Map<SalaryDetail, ValidationResult> employNotExistsError(SalaryMain salaryMain, List<SalaryDetail> tempSalary, List<EmployeeInfo> emp) {
        Map<SalaryDetail, ValidationResult> empNotExistMap = new HashMap<>();
        final Set<String> jobNumberSet = emp.stream().map(EmployeeInfo::getJobNumber).collect(Collectors.toSet());
        final List<SalaryDetail> unexists = tempSalary.stream().filter(i -> !jobNumberSet.contains(i.getJobNumber())).toList();
        unexists.forEach(i -> empNotExistMap.put(i, ValidationResult.fail("不在人员信息表中")));
        if (!empNotExistMap.isEmpty()) {
            salaryMain.salaryImportError();
        }
        return empNotExistMap;
    }


    /**
     * 姓名/工号存在重复
     * @param salaryMain 主体
     * @param tempSalary 数据
     * @return Map<SalaryDetail, ValidationResult>
     */
    public Map<SalaryDetail, ValidationResult> duplicatedJobNumberError(SalaryMain salaryMain, List<SalaryDetail> tempSalary) {
        Map<SalaryDetail, ValidationResult> dupMap = new HashMap<>();
        //工号重复
        Set<String> dupJobNumber = tempSalary.stream().collect(Collectors.groupingBy(SalaryDetail::getJobNumber)).keySet();
        dupJobNumber.forEach(num -> {
            final List<SalaryDetail> list = tempSalary.stream().filter(i -> Objects.equals(i.getJobNumber(), num)).toList();
            list.forEach(i -> {
                ValidationResult validationResult = ValidationResult.fail("工号重复");
                validationResult.addParameterResult("jobNumber");
                dupMap.put(i, validationResult);
            });
        });
        if (!dupMap.isEmpty()) {
            salaryMain.salaryImportError();
        }
        return dupMap;
    }

    /**
     * 存在离职人员
     * @param salaryMain 主体
     * @param tempSalary 数据
     * @param emp 员工信息
     * @return Map<SalaryDetail, ValidationResult>
     */
    public Map<SalaryDetail, ValidationResult> employeeQuitError(SalaryMain salaryMain, List<SalaryDetail> tempSalary, List<EmployeeInfo> emp) {
        Map<SalaryDetail, ValidationResult> quitErrorMap = new HashMap<>();
        Set<String> offList = emp.stream().filter(EmployeeInfo::isExit).map(EmployeeInfo::getJobNumber).collect(Collectors.toSet());
        tempSalary.stream().filter(i -> !offList.contains(i.getJobNumber())).forEach(i -> {
            quitErrorMap.put(i, ValidationResult.fail("离职人员"));
        });
        if (!quitErrorMap.isEmpty()) {
            salaryMain.salaryImportError();
        }
        return quitErrorMap;
    }

    //异常数据提醒
    @Override
    public ValidationResult salaryDetailRowCheck(SalaryDetail salaryDetail) {
        //返回异常信息类型及异常数据
        return ValidateUtil.validateEntity(salaryDetail);
    }

    /**
     * 合并表头
     * headMap: key=rowIndex(based 1), value = columnIndex(based 1), headerName
     * 合并规则：
     * 第一行是标题，忽略
     * 第二行 至 HEADER_ROW_NUM行：表头，多行是因为可能存在合并单元格情况
     * 同一列，优先取有值的；若一列存在多个值，则使用行号大的列的值，如2, 3行都有值，那么使用3行的值
     * 一般同一列多行有值表示同一类别下的多个分类
     */
    public Map<Integer, String> createHeader(Map<Integer, Map<Integer, String>> headMap) {
        Map<Integer, String> map = new HashMap<>();
        //key=colIndex, value=多个列值
        final Map<Integer, List<String>> colMap = headMap.values().stream()
                .flatMap(rowMap -> rowMap.entrySet().stream())
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));
        for (Map.Entry<Integer, List<String>> entry : colMap.entrySet()) {
            Integer colIndex = entry.getKey();
            String colName = "";
            List<String> colNameList = entry.getValue();
            for (String c : colNameList) {
                if (StringUtils.isNotBlank(c)) {
                    if (StringUtils.isNotBlank(colName)) {
                        colName = c;
                    }
                }
            }
            map.put(colIndex, colName);
        }
        return map;
    }

    //生成工资条
    public List<SalarySlip> createSalarySlips(List<SalaryDetail> list, SalaryMain main) {
        List<SalarySlip> slips = new ArrayList<>();
        list.forEach(detail -> {
            SalarySlip s = SalarySlip.create(main, detail, detail.getJobNumber(), detail.getNetPaid());
            slips.add(s);
        });
        return slips;
    }

    public void saveSalarySlips(List<SalarySlip> salarySlips) {
        salarySlipRepository.saveAll(salarySlips);
    }

    @Override
    public List<SalaryMainDTO> findSalaryMainByYearMonthAndGroup(String yearMonth, String group) {
        final List<SalaryMain> mainList = salaryMainRepository.findByYearMonthAndGroup(yearMonth, group);
        List<SalaryMainDTO> list = new ArrayList<>();
        mainList.forEach(i -> {
            SalaryMainDTO dto = new SalaryMainDTO();
            dto.setMain(i);
//            SalarySlipBoard board = new SalarySlipBoard();
//            final int checkCount = salarySlipRepository.countByIsCheckAndMainId(true, i.getId());
//            board.setCheckCount(checkCount);
//            final int readCount = salarySlipRepository.countByIsReadAndMainId(true, i.getId());
//            board.setReadCount(readCount);
//            final int sendCount = salarySlipRepository.countByIsSendAndMainId(true, i.getId());
//            board.setSendCount(sendCount);
//            dto.setSalarySlipBoard(board);
            list.add(dto);
        });
        return list;
    }

    //查看已发送的工资条
    public List<SalarySlip> findSalarySlipByMainIdAndSent(String mainId) {
//        return salarySlipRepository.findByMainIdAndSend(mainId, true);
        return null;
    }

    @Override
    public SalarySlipBoard createSalaryBoard(String mainId) {
        SalarySlipBoard board = new SalarySlipBoard();
        List<SalarySlip> slipList = new ArrayList<>();
//        final List<SalarySlip> slipList = salarySlipRepository.findByMainIdAndErrorIsNotEmpty(mainId);
        board.setSlipList(slipList);
        //统计数据
        final int unreadCount = (int)slipList.stream().filter(i -> !i.isRead()).count();
        final int uncheckCount = (int)slipList.stream().filter(i -> !i.isCheck()).count();
        board.setUnreadCount(unreadCount);
        board.setUncheckCount(uncheckCount);
        return board;
    }

    @Override
    public void saveSalary(List<SalaryDetail> list, SalaryMain salaryMain) {
        //保存设置
        salaryMainRepository.save(salaryMain);
        salaryDetailRepository.saveAll(list);
    }

    //下载模板
    public void downloadSalaryExcelModel(String group) {
        if (StringUtils.isNotBlank(group)) {
            //抽取人员数据
            final List<EmployeeInfo> list = employeeRepository.findByCompanyAndIsActive(group, "1");

        }
    }




    @Transactional
    @Override
    public void deleteSalaryAll(String id) {
        //1. 验证
        ValidateUtil.ensurePropertyNotnull(id, "SalaryMain:id");
        //2. TODO 权限

        //3. 删除主表
        salaryMainRepository.deleteById(id);
        //4. 删除关联表，如个人工资条
        salaryDetailRepository.deleteByMainId(id);

    }

    @Override
    public void saveSalaryDetailBatch(List<SalaryDetail> list) {
        //批量存储
        salaryDetailRepository.saveAllAndFlush(list);
    }

}