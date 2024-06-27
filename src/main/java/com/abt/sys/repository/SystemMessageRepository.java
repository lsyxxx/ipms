package com.abt.sys.repository;

import com.abt.sys.model.entity.SystemMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemMessageRepository extends JpaRepository<SystemMessage, String> {
    List<SystemMessage> findByToIdOrderByUpdateTimeDesc(String toId);
}