package com.ipms.sys.model.entity;

import java.util.Date;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.Id;

/**
 * 部门表(Dept)实体类
 *
 * @since 2023-07-31 09:54:20
 */
@Data
@NoArgsConstructor
@Schema(description = "部门表")
public class Dept implements Serializable {
    private static final long serialVersionUID = -11246881042711848L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private @Id Long id;
    /**
     * 部门编号
     */
    @Schema(description = "部门编号")
    private String deptNo;
    /**
     * 部门简称
     */
    @Schema(description = "部门简称")
    private String deptAbr;
    /**
     * 父部门id
     */
    @Schema(description = "父部门id")
    private Long parentId;
    /**
     * 部门显示顺序
     */
    @Schema(description = "部门显示顺序")
    private String sort;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;
    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Date updateTime;
    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private Long tenantId;
    /**
     * 删除标记，0未删除，1删除
     */
    @Schema(description = "删除标记，0未删除，1删除")
    private String deleteFlag;

}

