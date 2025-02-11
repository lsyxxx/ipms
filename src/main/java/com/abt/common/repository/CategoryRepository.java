package com.abt.common.repository;

import com.abt.common.entity.Category;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {
  List<Category> findByTypeId(@Size(max = 50) String typeId);
}