package com.ipms.sys.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private @Id Long  id;
    private String name;
    private String type;
    private String description;
    private boolean enabled;
    private String sort;
    private Long tenantId;
    private char deleteFlag;


    static Role of(String name, String type, String description, boolean enabled, String sort, Long tenantId, char deleteFlag) {
        return new Role(null, name, type, description, enabled, sort, tenantId, deleteFlag);
    }

}
