package com.abt.oa.model;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Getter
@Setter
public class FieldWorkRequestForm extends RequestForm {
    private String mode;
    private String yearMonth;
    private String company;
    private String dept;

    /**
     * 审批状态多选（优先于 {@link #getState()}）
     */
    private List<String> states;

    /**
     * 解析有效状态列表：优先 states；否则把 state 按逗号/分号拆分（兼容单选与 "通过,待审批"）
     */
    public List<String> effectiveStates() {
        if (states != null && !states.isEmpty()) {
            return states.stream()
                    .filter(StringUtils::isNotBlank)
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(getState())) {
            return Arrays.stream(getState().split("[,;]"))
                    .map(String::trim)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
