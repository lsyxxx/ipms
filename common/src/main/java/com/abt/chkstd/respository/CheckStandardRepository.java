package com.abt.chkstd.respository;

import com.abt.chkstd.entity.CheckStandard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckStandardRepository extends JpaRepository<CheckStandard, String> {
}