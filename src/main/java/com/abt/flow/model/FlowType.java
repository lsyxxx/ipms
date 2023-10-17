package com.abt.flow.model;

import com.abt.common.model.AuditInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 流程业务类型
 * 数据根源：FlowScheme
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
public class FlowType extends AuditInfo {

    /**
     * 唯一id
     */
    private String id;

    /**
     * 业务编码
     */
    private String code;

    /**
     * 流程类型名称
     */
    private String name;

    /**
     * 流程类型分类
     */
    private String type;

    /**
     * 流程类型具体描述
     */
    private String description;

    /**
     * 对应表单id
     */
    private String formId;

    /**
     * 对应表单类型
     */
    private int formType;

    /**
     * 流程所需权限，暂时有权限显示
     * 0: 完全公开, 1: 指定部门
     */
    private int authType = 0;

    /**
     * 是否已删除
     */
    private boolean isDeleted;

    /**
     * 是否启用
     */
    private boolean isEnabled;

    /**
     * 排序序号
     */
    private int sortCode;

    /**
     * 流程定义ID唯一
     */
    private String procDefId;

    /**
     * 对应服务
     */
    private String service;

    public String simpleInfo() {
        return
                "{id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
