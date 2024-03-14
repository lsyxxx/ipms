package com.abt.common.entity;

import com.abt.common.model.AuditInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 公司
 * 三级公司：一级->二级->三级
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Company extends AuditInfo {

    private String id;
    private String name;
    /**
     * 上级公司id
     */
    private String parentId;
    /**
     * 公司等级
     * 1，2，3
     */
    private int level;
    /**
     * 一级公司
     * 如果自身是则没有
     */
    private String firstTier;
    /**
     * 二级公司
     * 如果自身是二级公司则没有
     */
    private String secondTier;

}
