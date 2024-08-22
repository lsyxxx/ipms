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
public class Company {

    private String id;
    /**
     * 常用名称，如阿伯塔
     */
    private String name;
    /**
     * 代码，如ABT
     */
    private String code;
    /**
     * 简写代码，如A
     */
    private String shortCode;
    /**
     * 全称
     */
    private String fullName;
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

    public static Company of(String code, String name) {
        Company company = new Company();
        company.setCode(code);
        company.setName(name);
        return company;
    }

}
