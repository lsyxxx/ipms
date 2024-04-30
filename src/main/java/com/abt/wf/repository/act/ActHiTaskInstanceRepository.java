package com.abt.wf.repository.act;

import com.abt.wf.model.act.ActHiTaskInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ActHiTaskInstanceRepository extends JpaRepository<ActHiTaskInstance, String>, JpaSpecificationExecutor<ActHiTaskInstance> {
}