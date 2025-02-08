package com.abt.material.repository;

import com.abt.material.entity.MaterialDetail;
import jakarta.persistence.NamedEntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MaterialDetailRepository extends JpaRepository<MaterialDetail, String>, Specification<MaterialDetail> {

    @Query("select m from MaterialDetail m " +
            "where 1=1 " +
            "and (:deptId is null or :deptId = '' or m.deptId = :deptId) " +
            "or (m.deptId is null or m.deptId = '')"
    )
    Page<MaterialDetail> findByQueryPageable(String deptId, Pageable pageable);


}