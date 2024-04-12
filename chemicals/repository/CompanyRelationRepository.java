package com.abt.chemicals.repository;

import com.abt.chemicals.entity.Company;
import com.abt.chemicals.entity.CompanyRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompanyRelationRepository extends JpaRepository<CompanyRelation, String> {

    List<CompanyRelation> findByChemicalId(String chemicalId);

    /**
     * 查询化学品关联厂商的信息
     * @param chemicalId 化学品id
     */
    @Query(value = "select cc " +
            " from CompanyRelation cr left join Company cc on cr.companyId = cc.id" +
            " where cr.chemicalId = :chemicalId")
    List<Company> findCompanyByChemicalId(String chemicalId);

}
