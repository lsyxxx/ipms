package com.abt.chkmodule.repository;

import com.abt.chkmodule.entity.CheckStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CheckStandardRepository extends JpaRepository<CheckStandard, String> {


    @Query("""
    select rel.checkItemId,c
    from CheckStandard c
    left join CheckItemStandardRel rel on c.id = rel.standardId
    where rel.checkItemId in :checkItemIds
""")
    List<Object[]> findByCheckItemIds(List<String> checkItemIds);
}