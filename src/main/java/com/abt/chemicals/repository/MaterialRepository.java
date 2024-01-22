package com.abt.chemicals.repository;

import com.abt.chemicals.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, String> {
}
