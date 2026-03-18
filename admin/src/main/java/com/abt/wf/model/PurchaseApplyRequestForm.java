package com.abt.wf.model;

import camundajar.impl.scala.Array;
import com.abt.common.model.RequestForm;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 采购流程
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PurchaseApplyRequestForm extends RequestForm {
    /**
     * 列表是否包含详细记录
     */
    private boolean hasDetails = false;

    /**
     * 部门字符串
     */
    private String depts;

    private boolean deptIgnore = true;

    private List<String> deptList = new ArrayList<>();

    public void convertDeptList(){
        if (StringUtils.isNotBlank(depts)){
            this.deptList = new ArrayList<>();
            Collections.addAll(this.deptList, depts.split(","));
            this.deptIgnore = false;
        } else {
            deptList = null;
            this.deptIgnore = true;
        }
    }

}
