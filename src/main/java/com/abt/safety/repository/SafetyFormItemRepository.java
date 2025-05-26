package com.abt.safety.repository;

import com.abt.safety.entity.SafetyFormItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SafetyFormItemRepository extends JpaRepository<SafetyFormItem, String> {
    void deleteByFormId(String formId);
}