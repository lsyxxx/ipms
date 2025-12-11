package com.abt.testing.repository;

import com.abt.testing.entity.SampleRegist;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface SampleRegistRepository extends JpaRepository<SampleRegist, String> {

    /**
     * 查询去重的委托项目下的检测项目
     * @param entrustIds 委托编号集合
     */
    @Query(value = """
            select distinct entrustId, CheckModeuleId, CheckModeuleName
            from T_SampleRegist_CheckModeuleItem 
            where entrustId in (:entrustIds)
            """, nativeQuery = true)
    Set<Tuple> findDistinctCheckModulesByEntrustId(Set<String> entrustIds);


    List<SampleRegist> findByNewSampleNoIn(Collection<String> newSampleNos);
}