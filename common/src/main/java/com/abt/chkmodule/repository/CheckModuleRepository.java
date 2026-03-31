package com.abt.chkmodule.repository;

import com.abt.chkmodule.entity.CheckModule;
import com.abt.common.repository.EnabledRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckModuleRepository extends JpaRepository<CheckModule, String>, EnabledRepository<CheckModule, String> {


    boolean existsByName(String name);

}