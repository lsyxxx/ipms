package com.abt.wxapp.checkmodule.entity;

import com.abt.wxapp.checkmodule.entity.options.CheckOptions;
import com.abt.wxapp.checkmodule.model.CheckComponentType;
import com.abt.wxapp.checkmodule.model.PayType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 检测项目组件。{@link #type} 与 {@link #options} 中 JSON 的 {@code kind} 应对应同一类控件（如 input + kind=input）。
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckComponent {

    private String id;

    /**
     * 组件名称/标题。一般用于前端的标题展示
     */
    private String label;

    /**
     * 组件类型，如 select/checkbox/input 等
     */
    private CheckComponentType type;

    /**
     * 类型专属配置；序列化为 JSON 时带 {@code kind} 字段，与 {@link #type} 语义一致
     */
    private CheckOptions options;

    /**
     * 用户提示
     */
    private String tips;

    /**
     * 是否必填
     */
    private boolean required;

    /**
     * 本付款方式
     */
    private PayType payType;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 组件顺序，必须
     */
    private int sortNo;


    public CheckComponent() {
        this.id = UUID.randomUUID().toString();
    }



}
