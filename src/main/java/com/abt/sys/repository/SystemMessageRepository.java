package com.abt.sys.repository;

import com.abt.sys.model.entity.SystemMessage;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SystemMessageRepository extends JpaRepository<SystemMessage, String> {

    @Query("""
    select s from SystemMessage s
    where (s.toId = :toId)
    and ((:isRead = null) or  (:isRead = true and s.readTime is not null ) or (:isRead = false and s.readTime is null ) )
    and ('all' in :typeIds or s.typeId in :typeIds)
    order by s.createTime desc
""")
    Page<SystemMessage> findAllByToIdAndTypeIdsAndIsRead(String toId, List<String> typeIds, Boolean isRead, Pageable pageable);
}