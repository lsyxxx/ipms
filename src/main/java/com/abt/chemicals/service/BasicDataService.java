package com.abt.chemicals.service;

import com.abt.chemicals.entity.ChemicalType;

import java.util.List;

public interface BasicDataService {
    List<ChemicalType> queryType(String name, int level, String type1Id);

    List<ChemicalType> queryAllTypes();

    void deleteType(String id);
}
