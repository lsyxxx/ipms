package com.abt.safety.model;

/**
 * 检查表单类型
 */
public enum CheckType {
    SAFE("安全"),
    ENV("环境"),
    ;

    private String description;

    public String getDescription() {
        return description;
    }

    CheckType(String description) {
        this.description = description;
    }

    /**
     * 根据字符串获取对应的CheckType枚举值
     * @param value 字符串值，支持大小写不敏感
     * @return 对应的CheckType枚举值
     * @throws IllegalArgumentException 如果找不到对应的枚举值
     */
    public static CheckType fromString(String value) {
        if (value == null) {
            return null;
        }
        
        // 将输入转换为大写进行匹配
        String upperValue = value.toUpperCase();
        
        // 尝试直接匹配枚举名称
        for (CheckType type : CheckType.values()) {
            if (type.name().equals(upperValue)) {
                return type;
            }
        }
        
        // 如果没有匹配到，抛出异常
        throw new IllegalArgumentException("无效的CheckType值: " + value);
    }
}
