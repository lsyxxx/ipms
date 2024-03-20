package com.abt.wf.repository;

import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ReimburseRepository extends JpaRepository<Reimburse, String>, JpaSpecificationExecutor<Reimburse> {


}
