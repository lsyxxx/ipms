package com.abt.oa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 确认考勤请求
 */
@Getter
@Setter
public class FieldConfirmRequestForm {

    private String userid;
    private String yearMonth;
    private List<String> ids;



}
