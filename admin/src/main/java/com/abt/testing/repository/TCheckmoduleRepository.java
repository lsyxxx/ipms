package com.abt.testing.repository;

import com.abt.testing.entity.TCheckmodule;
import com.abt.testing.model.ReportCoverCheckModuleVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface TCheckmoduleRepository extends JpaRepository<TCheckmodule, String> {

    Page<TCheckmodule> findByIsActive(String isActive, Pageable pageable);

    /** 仅查询封面展示所需的项目 ID 和名称，避免加载旧系统其他字段。 */
    @Query("SELECT new com.abt.testing.model.ReportCoverCheckModuleVO(t.id, t.fname) " +
            "FROM TCheckmodule t WHERE t.id IN :ids")
    List<ReportCoverCheckModuleVO> findCoverCheckModules(@Param("ids") Collection<String> ids);
}
