package com.abt.wxapp.order.model;

import com.abt.chkmodule.entity.OptionData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 保存订单（下单）请求对象
 */
@Getter
@Setter
public class OrderRequestForm {

    /**
     * 动态表单配置ID，用于表示的是哪个表单
     */
    private Long schemeId;

    /**
     * 检测项目ID
     */
    private String checkModuleId;


    /**
     * ！！核心
     * 用户的输入值。比如输入的样品数量，选择的薄片制备+鉴定
     */
    private List<OptionData> optionDataList;

}
