package com.abt.testing.repository;

import com.abt.testing.entity.ReportCoverInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/** 报告封面信息数据访问。 */
public interface ReportCoverInfoRepository extends JpaRepository<ReportCoverInfo, String>,
        JpaSpecificationExecutor<ReportCoverInfo> {
    Optional<ReportCoverInfo> findByCoverName(String coverName);
}
