package com.abt.chemicals.entity;

import lombok.Data;

/**
 * 化学品材料
 */
@Data
public class Material {

    private String id;
    private String name;
    /**
     * main/aux
     * 主要/辅助
     */
    private String type;
    private String chemicalId;
}
