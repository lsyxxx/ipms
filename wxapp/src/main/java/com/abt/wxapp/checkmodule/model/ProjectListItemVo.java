package com.abt.wxapp.checkmodule.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 小程序检验检测项目列表项（与前端列表字段对齐）
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectListItemVo {
    private String id;
    private String name;
    /** 与分类接口 value 对齐，取自检测分类 code_ */
    private String checkModuleCategory;
    private String desc;
    private String duration;
    private String price;
    private String image;
    private String items;
    private List<String> certificate;
    private int status;
}
