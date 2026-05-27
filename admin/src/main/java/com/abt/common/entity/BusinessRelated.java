package com.abt.common.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 业务关联表
 * 表示一个业务数据关联其他业务数据。可能是不同的业务，可以一对多
 */
@Table(name = "biz_rel")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessRelated {

    private String id;

    /**
     * 原业务数据ID
     */
    private String sourceId;
    /**
     * 原业务类型
     */
    private String sourceBizType;
    /**
     * 原业务实体类class
     */
    private String sourceClass;
    /**
     * 原业务表名
     */
    private String sourceTable;


    /**
     * 关联的业务ID
     */
    private String targetId;
    private String targetBizType;
    private String targetClass;
    private String targetTable;
}
