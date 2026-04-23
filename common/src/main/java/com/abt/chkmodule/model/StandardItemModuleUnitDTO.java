package com.abt.chkmodule.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标准关联的检测子参数、检测项目及分类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardItemModuleUnitDTO {

    // --- 关系表信息 ---
    private String chapterNo;

    // --- 检测子参数(CheckItem)信息 ---
    private String itemId;
    private String itemCode;
    private String itemName;
    private String description;
    private Boolean isCma;
    private Boolean isCnas;
    private String restrict;

    // --- 检测项目(CheckModule)信息 ---
    private String moduleId;
    private String moduleName;

    // --- 项目分类(CheckUnit)信息 ---
    private String checkUnitId;
    private String checkUnitName;
}