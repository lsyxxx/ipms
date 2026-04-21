package com.abt.testing.repository;

import com.abt.testing.entity.TCheckunit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TCheckunitRepository extends JpaRepository<TCheckunit, String> {
    List<TCheckunit> findByIsActive(String isActive);
}