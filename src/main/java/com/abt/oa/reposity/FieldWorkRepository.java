package com.abt.oa.reposity;

import com.abt.oa.entity.FieldWork;
import com.abt.oa.model.FieldWorkRequestForm;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FieldWorkRepository extends JpaRepository<FieldWork, String> {

//    @Query("select fw from FieldWork fw ")
//    Page<FieldWork> findTodo(FieldWorkRequestForm form);
}