package com.abt.chemicals.entity;

import lombok.Data;

import java.util.List;

/**
 * 公司信息
 */
@Data
public class Company {
    private String id;
    /**
     * 全称（正式）
     */
    private String fullName;
    /**
     * 简称，一般称呼
     */
    private String name;
    private String address;
    private int sort;
    private boolean enable;

    /**
     * 营业执照附件id
     */
    private List<String> license;
}
