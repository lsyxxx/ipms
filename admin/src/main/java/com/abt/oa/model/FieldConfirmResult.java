package com.abt.oa.model;

import com.abt.oa.entity.FieldWork;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 考勤确认
 */
@Getter
@Setter
public class FieldConfirmResult {

    /**
     * 考勤列表
     */
    private List<FieldWork> records;
    /**
     * 考勤记录所属年月
     */
    private String yearMonth;

    /**
     * 根据当前考勤记录统计
     * key: 补贴名称
     * value: 统计结果，如天数等
     */
    private Map<String, Object> statMap;

}
