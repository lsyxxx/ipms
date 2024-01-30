package com.abt.chemicals.service;

import com.abt.chemicals.entity.ChemicalType;
import com.abt.chemicals.entity.Company;
import com.abt.chemicals.entity.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BasicDataService {
    List<ChemicalType> queryType(String name, int level, String type1Id);

    List<ChemicalType> queryAllTypes();

    List<ChemicalType> queryAllTypesEnabled();

    /**
     * 删除一级分类及该分类下所有二级分类
     * @param type1Id 一级分类id
     */
    long deleteType(String type1Id);

    /**
     * save or update
     */
    ChemicalType saveType(ChemicalType form);

    List<Company> queryAllCompanyByType(String type);

    List<Company> queryAllCompanyEnabled();

    List<Company> queryCompany(String type, String name);

    Company saveCompany(Company form);

    void deleteCompany(String id);

    /**
     * save or update
     */
    Product saveProduct(Product form);

    Page<Company> dynamicCompanyQuery(String name, String type, Boolean enable, Integer page, Integer size);

    void queryProductById(String id);
}
