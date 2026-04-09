package com.abt.testing.model;


import com.abt.chkmodule.model.ChannelEnum;
import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CheckModuleRequestForm extends RequestForm {

    /**
     * 所属检测分类 ID
     */
    private String checkUnitId;

    /**
     * 使用渠道
     */
    private ChannelEnum useChannel;

    /**
     * 是否无
     */
    private Boolean enabled;

    /**
     * 项目状态
     */
    private Integer status;

    /**
     * 资质：CMA, CNAS, other
     */
    private List<String> certificates;
}