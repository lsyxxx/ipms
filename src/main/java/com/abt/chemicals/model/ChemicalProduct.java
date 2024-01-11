package com.abt.chemicals.model;

import com.abt.chemicals.entity.Contact;
import lombok.Data;

import java.util.List;

/**
 * 化学品产品
 */
@Data
public class ChemicalProduct {

    private String id;
    private String name;
    private String chemicalName;
    /**
     * 检测标准
     */
    private List<String> standardCode;
    /**
     * 用途
     */
    private String usage;
    /**
     * 主材料
     */
    private List<String> mainMaterials;
    private List<String> auxiliaryMaterials;
    /**
     * 生产工艺
     */
    private String manufacturing;
    //--- 生产


    //--- 采购



}
