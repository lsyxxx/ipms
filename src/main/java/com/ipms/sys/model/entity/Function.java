package com.ipms.sys.model.entity;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 功能模块表(Function)实体类
 *
 * @author makejava
 * @since 2023-07-31 15:52:49
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@Schema(description = "")
public class Function implements Serializable {
    private static final long serialVersionUID = -12375984459586266L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private Long id;
    /**
     * 功能路径, 每一级为权限编号, 以"."分隔: 一级权限.二级权限.三级权限.n级
     */
    @Schema(description = "功能路径, 每一级为权限编号, 以'.'分隔: 一级权限.二级权限.三级权限.n级")
    private String path;
    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;
    /**
     * parent id
     */
    @Schema(description = "parent id")
    private Long pid;
    /**
     * 链接
     */
    @Schema(description = "链接")
    private String url;
    /**
     * 说明
     */
    @Schema(description = "说明")
    private String remark;
    /**
     * 组件
     */
    @Schema(description = "组件")
    private String component;
    /**
     * 收缩
     */
    @Schema(description = "收缩")
    private Boolean state;
    /**
     * 排序
     */
    @Schema(description = "排序")
    private String sort;
    /**
     * 启用
     */
    @Schema(description = "启用")
    private Boolean enabled;
    /**
     * 类型
     */
    @Schema(description = "类型")
    private String type;
    /**
     * 功能按钮
     */
    @Schema(description = "功能按钮")
    private String pushBtn;
    /**
     * 图标
     */
    @Schema(description = "图标")
    private String icon;
    /**
     * 删除标记，0未删除，1删除
     */
    @Schema(description = "删除标记，0未删除，1删除")
    private String deleteFlag;

}

