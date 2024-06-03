package com.abt.sys.repository;

import com.abt.sys.model.entity.SystemMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemMessageRepository extends JpaRepository<SystemMessage, String> {
}