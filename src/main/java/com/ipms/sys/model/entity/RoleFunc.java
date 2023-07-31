package com.ipms.sys.model.entity;

import java.util.Date;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.Id;

/**
 * 角色-功能关联表(RoleFunc)实体类
 *
 * @author makejava
 * @since 2023-07-31 09:54:20
 */
@Data
@NoArgsConstructor
@Schema(description = "角色-功能关联表")
public class RoleFunc implements Serializable {
    private static final long serialVersionUID = -39361324218546704L;
    /**
     * pk
     */
    @Schema(description = "pk")
    private @Id Long id;
    /**
     * sys_role: id
     */
    @Schema(description = "sys_role: id")
    private Long roleId;
    /**
     * sys_function: id
     */
    @Schema(description = "sys_function: id")
    private Long funcId;
    /**
     * username
     */
    @Schema(description = "username")
    private String createUser;

    @Schema(description = "${column.comment}")
    private Date createDate;

}

