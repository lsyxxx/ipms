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
     * testItem.id
     */
    private String testItemId;

    /**
     * 已结算的结算单号
     */
    private String mid;

    /**
     * 临时key
     */
    private String tempKey;

    /**
     * 已结算的key
     */
    private String key;


    private String genKey() {
        this.key = String.join("_", sampleNo, checkModuleId);
        return this.key;
    }

    private String genTempKey() {
        this.tempKey = String.join("_", tempKey, tempCheckModuleId);
        return this.tempKey;
    }


    /**
     * 是否是重复结算
     */
    public boolean isDuplicated() {
        if (StringUtils.isEmpty(sampleNo)) {
            return false;
        }
        String key1 = String.join("_", tempSampleNo, tempCheckModuleId);
        String key2 = String.join("_", sampleNo, checkModuleId);
        return key1.equals(key2);
    }
}
