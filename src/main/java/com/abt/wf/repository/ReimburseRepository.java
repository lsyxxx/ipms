package com.abt.wf.repository;

import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReimburseRepository extends JpaRepository<Reimburse, String> {


}
