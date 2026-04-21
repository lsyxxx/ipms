package com.abt.testing.repository;

import com.abt.testing.entity.TCheckmodule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TCheckmoduleRepository extends JpaRepository<TCheckmodule, String> {

    Page<TCheckmodule> findByIsActive(String isActive, Pageable pageable);
}