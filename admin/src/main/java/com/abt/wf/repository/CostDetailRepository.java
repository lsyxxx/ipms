package com.abt.wf.repository;

import com.abt.common.config.ValidateGroup;
import com.abt.wf.entity.CostDetail;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CostDetailRepository extends JpaRepository<CostDetail, String> {
    List<CostDetail> findByRefCode(@NotNull(groups = ValidateGroup.Save.class, message = "关联单号(refCode)不能为空") String refCode);
}