package com.abt.sys.repository;

import com.abt.sys.model.entity.DataPrivilegeRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataPrivilegeRuleRepository extends JpaRepository<DataPrivilegeRule, String> {

  DataPrivilegeRule findBySourceCodeAndEnable(String sourceCode, Boolean enable);

}