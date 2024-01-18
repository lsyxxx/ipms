package com.abt.chemicals.repository;

import com.abt.chemicals.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CompanyRepository extends JpaRepository<Company, String> {

    List<Company> findByType(@Param("type_") String type);
}
