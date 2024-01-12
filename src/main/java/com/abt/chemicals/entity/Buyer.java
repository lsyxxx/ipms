package com.abt.chemicals.entity;

import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 采购商
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Buyer extends Company{

    @Transient
    private List<Price> priceList;
    @Transient
    private List<Contact> contactList;



}
