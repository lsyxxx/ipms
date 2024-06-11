package com.abt.salary.model;

import com.abt.common.model.ValidationResult;
import com.abt.salary.entity.SalaryDetail;
import com.abt.salary.entity.SalaryMain;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上传工资预览
 */
@Data
public class SalaryPreview {

    /**
     * 异常检验结果
     */
    private Map<String, Map<SalaryDetail, ValidationResult>> typedErrorMap = new HashMap<>();

    /**
     * 预览数据
     */
    private List<SalaryDetail> salaryDetails = new ArrayList<>();
    private SalaryMain salaryMain;

    public static SalaryPreview create(SalaryMain salaryMain) {
        SalaryPreview salaryPreview = new SalaryPreview();
        salaryPreview.setSalaryMain(salaryMain);
        return salaryPreview;
    }

    public SalaryPreview addErrorMap(String typeName, Map<SalaryDetail, ValidationResult> errorMap) {
        typedErrorMap.put(typeName, errorMap);
        return this;
    }

}
