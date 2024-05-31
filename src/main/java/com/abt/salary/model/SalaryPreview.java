package com.abt.salary.model;

import com.abt.common.model.ValidationResult;
import com.abt.salary.entity.SalaryDetail;
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

    public static SalaryPreview create() {
        return new SalaryPreview();
    }

    public SalaryPreview addErrorMap(String typeName, Map<SalaryDetail, ValidationResult> errorMap) {
        typedErrorMap.put(typeName, errorMap);
        return this;
    }

}
