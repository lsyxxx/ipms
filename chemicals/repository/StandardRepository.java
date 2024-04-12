package com.abt.chemicals.repository;

import com.abt.chemicals.entity.Standard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StandardRepository extends JpaRepository<Standard, String> {

    List<Standard> findByChemicalIdOrderByNameAsc(String chemicalId);

}
