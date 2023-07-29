package com.ipms.sys.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "角色信息表")
public class Role {
    @Schema(description = "PK")
    private @Id Long  id;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "类型")
    private String type;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "是否启用")
    private boolean enabled;
    @Schema(description = "排序")
    private String sort;
    @Schema(description = "租户名称")
    private Long tenantId;
    @Schema(description = "是否删除")
    private char deleteFlag;


    static Role of(String name, String type, String description, boolean enabled, String sort, Long tenantId, char deleteFlag) {
        return new Role(null, name, type, description, enabled, sort, tenantId, deleteFlag);
    }

}
