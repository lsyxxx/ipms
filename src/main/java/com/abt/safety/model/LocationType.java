package com.abt.safety.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * 地点类型，野外/室内
 */
@Getter
public enum LocationType {
    ROOM("室内"),
    FIELD("野外"),
    ;

    private final String description;
    
    public String getName() {
        return this.name();
    }

    LocationType(String desc) {
        this.description = desc;
    }

    /**
     * 根据字符串获取对应的LocationType枚举值
     * @param value 字符串值，支持大小写不敏感
     * @return 对应的LocationType枚举值
     * @throws IllegalArgumentException 如果找不到对应的枚举值
     */
    public static LocationType fromString(String value) {
        if (value == null) {
            return null;
        }

        // 将输入转换为大写进行匹配
        String upperValue = value.toUpperCase();

        // 尝试直接匹配枚举名称
        for (LocationType type : LocationType.values()) {
            if (type.name().equals(upperValue)) {
                return type;
            }
        }

        // 如果没有匹配到，抛出异常
        throw new IllegalArgumentException("无效的LocationType值: " + value);
    }

}
