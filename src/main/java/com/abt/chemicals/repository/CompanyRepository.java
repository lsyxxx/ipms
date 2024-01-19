package com.abt.chemicals.repository;

import com.abt.chemicals.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CompanyRepository extends JpaRepository<Company, String> {

    List<Company> findByTypeOrderBySortAsc(@Param("type_") String type);
    List<Company> findByTypeAndNameContainingOrderBySortAsc(@Param("type_") String type, @Param("name_") String name);
}
