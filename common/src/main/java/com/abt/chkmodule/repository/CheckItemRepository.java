package com.abt.chkmodule.repository;

import com.abt.chkmodule.entity.CheckItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckItemRepository extends JpaRepository<CheckItem, String> {
}