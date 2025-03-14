package com.abt.oa.reposity;

import com.abt.common.config.ValidateGroup;
import com.abt.oa.entity.OrgLeader;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrgLeaderRepository extends JpaRepository<OrgLeader, String> {
    @Transactional
    @Modifying
    void deleteByJobNumberAndDeptId(@NotNull(message = "负责人工号(jobNumber)不能为空", groups = {ValidateGroup.All.class}) String jobNumber, @NotNull(message = "负责部门id(deptId)不能为空", groups = {ValidateGroup.All.class}) String deptId);

    @Transactional
    @Modifying
    void deleteByJobNumber(String jobNumber);

    List<OrgLeader> findByJobNumber(@NotNull(message = "负责人工号(jobNumber)不能为空", groups = {ValidateGroup.All.class}) String jobNumber);
}