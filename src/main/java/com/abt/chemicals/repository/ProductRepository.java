package com.abt.chemicals.repository;

import com.abt.chemicals.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 */
public interface ProductRepository extends JpaRepository<Product, String> {
}
