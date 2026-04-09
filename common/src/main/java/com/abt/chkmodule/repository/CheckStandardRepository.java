package com.abt.chkmodule.repository;

import com.abt.chkmodule.entity.CheckStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CheckStandardRepository extends JpaRepository<CheckStandard, String> {


    @Query("""
    select c
    from CheckStandard c
    left join CheckItemStandardRel rel on c.id = rel.standardId
    where rel.checkItemId in :checkItemIds
""")
    List<CheckStandard> findByCheckItemIds(List<String> checkItemIds);


    @Query("""
            SELECT c
            FROM CheckStandard c
            JOIN CheckItemStandardRel rel ON c.id = rel.standardId
            WHERE rel.checkItemId = :itemId
            """)
    List<CheckStandard> findStandardsByItemId(String itemId);



}