package com.abt.chemicals.entity;

import lombok.Data;

import java.util.List;

/**
 * 化学品
 */
@Data
public class Product {
    private String id;
    private String name;
    private String type1;
    private String type2;
    private String usage;
}
