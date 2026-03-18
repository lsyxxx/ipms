package com.abt.oa.reposity;

import com.abt.oa.entity.FieldWorkItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FieldWorkItemRepository extends JpaRepository<FieldWorkItem, String> {

}