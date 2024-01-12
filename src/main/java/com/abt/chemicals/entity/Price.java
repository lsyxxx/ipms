package com.abt.chemicals.entity;

import lombok.Data;

import java.time.LocalDate;

/**
 * 报价
 */
@Data
public class Price {

    private String id;
    private double price;
    private LocalDate date;
    /**
     * 公司Id
     */
    private String companyId;
    /**
     * 化学品id
     */
    private String chemicalId;
}
