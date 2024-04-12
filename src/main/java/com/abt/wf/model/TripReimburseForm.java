package com.abt.wf.model;

import com.abt.wf.entity.TripReimburse;
import com.abt.wf.entity.WorkflowBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 差旅报销form
 * 公共信息包含在item中
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TripReimburseForm extends WorkflowBase{

    private String comment;
    private String decision;
    private String rootId;
    /**
     * 抄送
     */
    private List<String> copyList = new ArrayList<>();


    /**
     * 公共数据
     */
    private TripReimburse common;

    /**
     * 差旅报销明细，多条记录
     */
    private List<TripReimburse> items = new ArrayList<>();


    private Map<String, Object> variableMap = new HashMap<>();

    public static final String KEY_STARTER = "starter";
    public static final String KEY_MANAGER = "managerList";
    public static final String KEY_COST = "cost";

    public Map<String, Object> variableMap() {
        variableMap.put(KEY_STARTER, this.common.getCreateUserid());
        variableMap.put(KEY_COST, this.calcSum());
        if (StringUtils.isBlank(this.common.getManagers())) {
            variableMap.put(KEY_MANAGER, List.of());
        } else {
            variableMap.put(KEY_MANAGER, List.of(this.common.getManagers().split(",")));
        }
        return this.variableMap;
    }

    /**
     * 计算总金额，并赋值给common
     */
    public BigDecimal calcSum() {
        final BigDecimal sum = this.items.stream().map(TripReimburse::sumItem).reduce(BigDecimal.ZERO, BigDecimal::add);
        this.getCommon().setSum(sum);
        return sum;
    }





}
