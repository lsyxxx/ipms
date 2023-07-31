package com.ipms.sys.model.entity;

import java.util.Date;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.Id;

/**
 * 用户-角色关联表(UserRole)实体类
 *
 * @author makejava
 * @since 2023-07-31 09:54:20
 */
@Data
@NoArgsConstructor
@Schema(description = "用户-角色关联表")
public class UserRole implements Serializable {
    private static final long serialVersionUID = -30502242870693425L;
    /**
     * pk
     */
    @Schema(description = "pk")
    private @Id Long id;
    /**
     * sys_user: id
     */
    @Schema(description = "sys_user: id")
    private Long userId;
    /**
     * sys_role: id
     */
    @Schema(description = "sys_role: id")
    private Long roleId;
    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private Long createUser;
    /**
     * 创建日期
     */
    @Schema(description = "创建日期")
    private Date createDate;
    /**
     * 删除标志：0未删除,1已删除
     */
    @Schema(description = "删除标志：0未删除,1已删除")
    private Boolean deleteFlag;

}

