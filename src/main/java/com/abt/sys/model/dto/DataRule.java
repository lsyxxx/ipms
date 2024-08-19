package com.abt.sys.model.dto;

import com.abt.common.util.JsonUtil;
import com.abt.sys.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 权限
 */
@Data
@Slf4j
public class DataRule {
    /**
     * 操作符
     */
    @JsonProperty("Operation")
    private String operation;
    @JsonProperty("Filters")
    private List<DataFilter> filters;

    public static final String OPERATION_OR = "or";

    public static final String OPERATION_AND = "and";

    public boolean isOr() {
        return OPERATION_OR.equals(operation);
    }


    public static DataRule create(String json) throws JsonProcessingException {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        return JsonUtil.toObject(json, new TypeReference<DataRule>() {});
    }

    /**
     * 验证filter
     */
    public boolean doFilterChain(UserView userView) {
        if (this.filters == null) {
            //没有控制器，通过
            return true;
        }
        if (OPERATION_OR.equals(this.operation)) {
            //任意一个通过
            return filters.stream().anyMatch(f -> f.doFilter(userView));
        } else if (OPERATION_AND.equals(this.operation)) {
            return filters.stream().allMatch(f -> f.doFilter(userView));
        } else {
            log.warn("未处理的比较符 - {}。无法处理过滤器，数据校验不通过", this.operation);
            throw new BusinessException(String.format("未处理的比较符 - %s。无法处理过滤器，数据校验不通过", this.operation));
        }
    }



}
