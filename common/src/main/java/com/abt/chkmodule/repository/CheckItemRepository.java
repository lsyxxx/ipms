package com.abt.chkmodule.repository;

import com.abt.chkmodule.entity.CheckItem;
import com.abt.chkmodule.entity.CheckModule;
import com.abt.common.repository.EnabledRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CheckItemRepository extends JpaRepository<CheckItem, String>, EnabledRepository<CheckModule, String> {

}