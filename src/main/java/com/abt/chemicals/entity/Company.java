package com.abt.chemicals.entity;

import jakarta.persistence.Transient;
import lombok.Data;

import java.util.List;

/**
 * 公司信息
 */
@Data
public class Company {
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
     * 备注
     */
    private String note;

    /**
     * 营业执照附件id
     */
    @Transient
    private List<String> license;
}
