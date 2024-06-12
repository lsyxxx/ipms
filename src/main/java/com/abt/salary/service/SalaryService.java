package com.abt.salary.service;

import com.abt.common.model.ValidationResult;
import com.abt.salary.entity.SalaryDetail;
import com.abt.salary.entity.SalaryMain;
import com.abt.salary.model.SalaryMainDTO;
import com.abt.salary.model.SalaryPreview;
import com.abt.salary.model.SalarySlipBoard;
import com.abt.sys.model.entity.SystemFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface SalaryService {

    /**
     * 保存上传的Excel文件
     * @param file 上传文件
     */
    SystemFile saveSalaryExcel(MultipartFile file, String yearMonth);

    SalaryMain createSalaryMain(String yearMonth, String group, String netPaidColumnName, String excelPath, String excelName);

    /**
     * 工资表预览及校验
     * @param inputStream 工资表
     * @param sheetNo 默认第一个sheet
     * @param salaryMain 主体
     * @return SalaryPreview
     */
    SalaryPreview previewSalaryDetail(InputStream inputStream, int sheetNo, SalaryMain salaryMain);

    /**
     * 每行数据校验
     * @param salaryDetail 一行明细数据
     */
    ValidationResult salaryDetailRowCheck(SalaryDetail salaryDetail);

    List<SalaryMainDTO> findSalaryMainByYearMonthAndGroup(String yearMonth, String group);

    /**
     * 读取工资条面板
     */
    SalarySlipBoard createSalaryBoard(String mainId);

    /**
     * 保存工资表数据
     * @param list 明细
     * @param salaryMain 主体
     */
    void saveSalary(List<SalaryDetail> list, SalaryMain salaryMain);

    /**
     * 删除工资表及相关数据
     * @param id main id
     */
    void deleteSalaryAll(String id);

    /**
     * 批量保存明细（仅保存明细数据）
     * @param list 明细数据
     */
    void saveSalaryDetailBatch(List<SalaryDetail> list);
}
