package com.abt.market.model;

import cn.idev.excel.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 重复结算校验
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidateDuplicatedSampleDTO {

    private String tempId;

    /**
     * 导入的检测编号
     * 临时表stlm_test_temp的sample_no
     */
    private String tempSampleNo;
    /**
     * 导入的检测项目id
     */
    private String tempCheckModuleId;
    /**
     * 导入的检测项目名称
     */
    private String tempCheckModuleName;
    /**
     * 已结算的样品编号
     */
    private String sampleNo;
    /**
     * 已结算的检测项目
     */
    private String checkModuleId;
    /**
     * 已结算的检测项目名称
     */
    private String checkModuleName;

    /**
     * 已结算的结算单号
     */
    private String mid;

    /**
     * 是否是重复结算
     */
    public boolean isDuplicated() {
        if (StringUtils.isEmpty(sampleNo)) {
            return false;
        }
        String key1 = String.join("_", tempSampleNo, tempCheckModuleId, tempCheckModuleName);
        String key2 = String.join("_", sampleNo, checkModuleId, checkModuleName);
        return key1.equals(key2);
    }
}
