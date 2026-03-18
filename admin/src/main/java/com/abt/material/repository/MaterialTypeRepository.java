package com.abt.material.repository;

import com.abt.material.entity.MaterialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MaterialTypeRepository extends JpaRepository<MaterialType, String>, JpaSpecificationExecutor<MaterialType> {

    @Query("select m from MaterialType m " +
            "where (:isDeleted is null or m.isDeleted = :isDeleted) " +
            "and ('all' in :ids or m.id in :ids) " +
            "and (:nameLike is null or :nameLike = '' or m.name like %:nameLike%) " +
            "order by m.index")
    List<MaterialType> findByQuery(List<String> ids, String nameLike, Boolean isDeleted);


    List<MaterialType> findByNameContaining(String name);


}