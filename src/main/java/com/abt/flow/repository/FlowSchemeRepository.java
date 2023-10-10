package com.abt.flow.repository;

import com.abt.flow.model.entity.FlowScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 *
 */
public interface  FlowSchemeRepository extends JpaRepository<FlowScheme, String> {
}
