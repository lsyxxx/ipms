package com.abt.chemicals.entity;

import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 生产商
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Producer extends Company{

    /**
     * 化学品型号
     */
    private String code;

    @Transient
    private List<Contact> contactList;
    @Transient
    private List<Price> priceList;
}
