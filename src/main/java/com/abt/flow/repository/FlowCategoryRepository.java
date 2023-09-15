package com.abt.flow.repository;

import com.abt.flow.model.entity.FlowCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlowCategoryRepository extends JpaRepository<FlowCategory, String> {
}
