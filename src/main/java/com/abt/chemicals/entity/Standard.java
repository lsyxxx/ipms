package com.abt.chemicals.entity;

import com.abt.chemicals.model.StandardType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标准
 */
@Data
public class Standard {
    private String id;
    private String name;
    private String code;
    private String description;
    /**
     * 国标/行标/企业标准/地方/...
     * @see StandardType
     */
    private String type;
    /**
     * 是否使用
     */
    private boolean enable = true;
    private String fileId;
    private String fileUrl;
    /**
     * 发行时间
     */
    private LocalDateTime publishTime;
}
