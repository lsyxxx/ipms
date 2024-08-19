package com.abt.sys.model.dto;

import com.abt.sys.model.entity.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限数据过滤器
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
public class DataFilter {

    @JsonProperty("Key")
    private String key;
    @JsonProperty("Value")
    private String value;
    /**
     * 比较符号
     */
    @JsonProperty("Contrast")
    private String contrast;

    @JsonProperty("names")
    private String names;
    @JsonProperty("Text")
    private String text;



    public static final String KEY_LOGIN_USER = "{loginUser}";
    public static final String KEY_LOGIN_ROLE = "{loginRole}";
    public static final String KEY_LOGIN_DEPT = "{loginOrg}";

    /**
     * 等于
     */
    public static final String CONTRAST_EQUAL = "==";
    /**
     * 包含任意
     */
    public static final String CONTRAST_CONTAINS = "intersect";


    /**
     * 是否是角色控制权限
     */
    public boolean isRoleRule() {
        return KEY_LOGIN_ROLE.equals(key);
    }

    /**
     * 用户控制权限
     */
    public boolean isUserRule() {
        return KEY_LOGIN_USER.equals(key);
    }


    public boolean doFilter(UserView user) {
        Set<String> set = new HashSet<>();
        if (isRoleRule()) {
            set = user.getAuthorities().stream().map(Role::getId).collect(Collectors.toSet());
            this.doFilter(set);
        } else if (isUserRule()) {
            set.add(user.getId());
        }
        return this.doFilter(set);
    }

    /**
     * 验证
     * @param input - 比较的值
     */
    private boolean doFilter(Set<String> input) {
        if (StringUtils.isBlank(this.value)) {
            return false;
        }
        final Set<String> split = Set.of(this.value.split(","));
        if (CONTRAST_EQUAL.equals(contrast)) {
            //权限等于多个话，表示不同权限都要满足
            return split.containsAll(input);
        } else if (CONTRAST_CONTAINS.equals(contrast)) {
            return input.stream().anyMatch(split::contains);
        } else {
            //其他操作符
            log.warn("未处理的操作符{}。未能处理该权限 ", contrast);
            return false;
        }
    }


}
