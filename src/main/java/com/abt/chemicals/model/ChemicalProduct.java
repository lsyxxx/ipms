package com.abt.chemicals.model;

import com.abt.chemicals.entity.*;
import com.abt.sys.model.entity.SystemFile;
import lombok.Data;

import java.util.List;

/**
 * 化学品产品
 * 包含所有
 */
@Data
public class ChemicalProduct {

    private String id;
    private String name;
    private ChemicalType type1;
    private ChemicalType type2;
    private List<Standard> standards;
    private String usage;
    private List<Material> mainMaterials;
    private List<Material> auxiliaryMaterials;
    /**
     * 使用说明书附件id
     */
    private List<SystemFile> instructions;
    private String manufacturing;

    //--- 生产 producer
    private List<Producer> producers;

    //--- 采购 buyer
    private List<Buyer> buyers;



}
