package com.ipms.sys.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

/**
 * 功能表
 * sys_function
 */
@Data
public class Function {

    private @Id Long id;
    /**
     * 编号
     */
    private String number;
    private String name;
    /**
     * 上级编号
     */
    private String parentNumber;
    private String url;
    private String remark;
    private String component;
    private boolean state;
    private String sort;
    private boolean enabled;
    private String type;
    private String deleteFlag;


}
