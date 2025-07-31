package com.abt.market.repository;

import com.abt.market.entity.TestItem;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface TestItemRepository extends JpaRepository<TestItem, String> {
  List<TestItem> findByMid(String mid);

  void deleteByMid(String mid);

  /**
   *  查询委托单未结算的样品
   * @param entrustIds 委托单号列表
   */
  @Query(value = """
          select  sr.NewSampleNo as sample_no, sr.entrustId as entrust_id, scmi.CheckModeuleId as check_module_id, scmi.CheckModeuleName as check_module_name, 
                sr.OldSampleNo as old_sample_no, sr.Jname as well_no
          from T_entrust e
          left join T_SampleRegist sr on e.Id = sr.entrustId
          left join T_SampleRegist_CheckModeuleItem scmi on scmi.SampleRegistId = sr.NewSampleNo
          left join stlm_test st on sr.NewSampleNo = st.sample_no and st.check_module_id = scmi.CheckModeuleId
          where e.Id in :entrustIds
          and st.sample_no is null
          order by e.Id , sr.NewSampleNo, scmi.CheckModeuleId
   """, nativeQuery = true)
  List<Tuple> findUnsettledSamples(Set<String> entrustIds);
}