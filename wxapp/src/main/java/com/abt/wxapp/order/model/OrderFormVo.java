package com.abt.wxapp.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 下单页：检测项目 + 最新表单 id（与小程序 mock 结构对齐）
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderFormVo {
    private Long formSchemaId;
    private String checkModuleId;
    private String checkModuleName;
    private String orderNotice;
    private String description;
}
