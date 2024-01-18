package com.abt.chemicals.service;

import com.abt.chemicals.entity.ChemicalType;
import com.abt.chemicals.entity.Company;

import java.util.List;

public interface BasicDataService {
    List<ChemicalType> queryType(String name, int level, String type1Id);

    List<ChemicalType> queryAllTypes();

    /**
     * 删除一级分类及该分类下所有二级分类
     *
     * @param id 一级分类id
     * @return
     */
    long deleteType(String id);

    ChemicalType editType(ChemicalType form);

    List<Company> queryAllCompanyByType(String type);
}
