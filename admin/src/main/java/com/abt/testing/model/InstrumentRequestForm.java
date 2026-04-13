package com.abt.testing.model;

import com.abt.common.model.RequestForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

/**
 * 设备管理-分页查询参数表单
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InstrumentRequestForm extends RequestForm {

    /**
     * 分类：可多选
     */
    private List<String> types;

    /**
     * 设备状态：单选
     */
    private String status;

    /**
     * 使用部门：可多选
     */
    private List<String> useDepts;
}