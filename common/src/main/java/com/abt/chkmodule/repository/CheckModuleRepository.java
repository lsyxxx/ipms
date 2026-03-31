package com.abt.chkmodule.repository;

import com.abt.chkmodule.entity.CheckModule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckModuleRepository extends JpaRepository<CheckModule, String>{


    boolean existsByName(String name);

}