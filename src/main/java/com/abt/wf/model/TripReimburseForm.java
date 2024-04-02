package com.abt.wf.model;

import com.abt.wf.entity.TripReimburse;
import com.abt.wf.entity.WorkflowBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 差旅报销form
 * 公共信息包含在item中
 */
@Data
public class TripReimburseForm {

    private String comment;
    private String decision;


    /**
     * 公共数据
     */
    private TripReimburse common;

    /**
     * 差旅报销明细，多条记录
     */
    private List<TripReimburse> items;


    private Map<String, Object> variableMap = new HashMap<>();

    public static final String KEY_STARTER = "starter";

    public Map<String, Object> variableMap() {
        //流程发起
        variableMap.put(KEY_STARTER, this.common.getCreateUserid());
        return this.variableMap;
    }



}
