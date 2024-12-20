package com.abt.sys.model.dto;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 查询组织机构
 */
@Getter
@Setter
public class OrgRequestForm extends RequestForm {

    private String cascadeId;

    private Integer status;

    /**
     * 多个部门id
     */
    private List<String> orgIds;

}
