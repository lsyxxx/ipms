package com.abt.chkmodule.repository;

import com.abt.chkmodule.entity.CheckModule;
import com.abt.chkmodule.entity.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CheckModuleRepository extends JpaRepository<CheckModule, String> {


    boolean existsByName(String name);

}