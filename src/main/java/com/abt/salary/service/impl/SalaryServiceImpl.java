package com.abt.salary.service.impl;

import com.abt.common.model.ValidationResult;
import com.abt.common.util.ValidateUtil;
import com.abt.salary.entity.SalaryDetail;
import com.abt.salary.entity.SalaryMain;
import com.abt.salary.repository.SalaryDetailRepository;
import com.abt.salary.repository.SalaryMainRepository;
import com.abt.salary.service.SalaryExcelReadListener;
import com.abt.salary.service.SalaryService;
import com.abt.sys.exception.BusinessException;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.support.ExcelTypeEnum;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.View;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Slf4j
@Service
public class SalaryServiceImpl implements SalaryService {

    private final View error;
    private final SalaryMainRepository salaryMainRepository;
    private final SalaryDetailRepository salaryDetailRepository;

    @Value("${sl.title.format}")
    private String salaryTitleFormat;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");


    public SalaryServiceImpl(SalaryDetailRepository salaryDetailRepository, View error, SalaryMainRepository salaryMainRepository) {
        this.salaryDetailRepository = salaryDetailRepository;
        this.error = error;
        this.salaryMainRepository = salaryMainRepository;
    }


    @Override
    public void readSalaryDetail(String fileName, String sheetName) {

    }

    @Override
    public SalaryMain createSalaryDetail(String yearMonth, String group, String netPaidColumnName) {
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
    public void readSalaryDetail(InputStream inputStream, String sheetName, SalaryMain salaryMain) {
        //读取excel
        SalaryExcelReadListener listener = new SalaryExcelReadListener(this, salaryMain.getId());
        EasyExcel.read(inputStream, SalaryDetail.class, listener)
                // 需要读取批注 默认不读取
//                .extraRead(CellExtraTypeEnum.COMMENT)
                // 需要读取超链接 默认不读取
//                .extraRead(CellExtraTypeEnum.HYPERLINK)
                // 需要读取合并单元格信息 默认不读取
                .excelType(ExcelTypeEnum.XLSX)
                .headRowNumber(3)
                .extraRead(CellExtraTypeEnum.MERGE).sheet(sheetName).doRead();
        final Map<SalaryDetail, ValidationResult> errorDetailMap = listener.getErrorDetailMap();
        final Map<Integer, Map<Integer, String>> headMap = listener.getHeadMap();
        System.out.println("headMap: " + headMap.toString());
        if (errorDetailMap != null) {
            errorDetailMap.forEach((key, value) -> {
                System.out.println("数据: {" + key.getName() + "-" + key.getJobNumber() + "} 错误: " + value.toString());
            });
            salaryMain.setState(SalaryMain.STATE_ERROR);

        } else {
            salaryMain.setState(SalaryMain.STATE_SUCCESS);
        }
        salaryMainRepository.save(salaryMain);
    }




    public ValidationResult salaryDetailOtherCheck() {
        //异常校验：TODO 校验人员是否与Excel一致
        //异常校验: TODO 重复姓名

        return ValidationResult.pass();
    }

    //异常数据提醒
    @Override
    public ValidationResult salaryDetailRowCheck(SalaryDetail salaryDetail) {
        //返回异常信息类型及异常数据
        return ValidateUtil.validateEntity(salaryDetail);
    }

    //合并表头
    public void combineHeader() {

    }

    public void loadSalaryBill(String yearMonth, String group) {
        final List<SalaryMain> mainList = salaryMainRepository.findByYearMonthAndGroup(yearMonth, group);
        //统计数据
        if (!mainList.isEmpty()) {
            mainList.forEach(m -> {
                String mid = m.getId();
                //已发送

                //已查看
                //已确认
            });
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
