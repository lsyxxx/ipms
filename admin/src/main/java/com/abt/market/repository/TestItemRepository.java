package com.abt.market.repository;

import com.abt.market.entity.TestItem;
import com.abt.market.model.ValidateDuplicatedSampleDTO;

import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface TestItemRepository extends JpaRepository<TestItem, String> {
  List<TestItem> findByMid(String mid);

  void deleteByMid(String mid);

  /**
   *  查询委托单未结算的样品
   *  T_SampleRegist中可能有样品没有配置项目
   * @param entrustIds 委托单号列表
   */
  @Query(value = """
   select * from (
         select  sr.NewSampleNo as sample_no, sr.entrustId as entrust_id, scmi.CheckModeuleId as check_module_id, scmi.CheckModeuleName as check_module_name, 
                sr.OldSampleNo as old_sample_no, sr.Jname as well_no
          from T_entrust e
          left join T_SampleRegist sr on e.Id = sr.entrustId
          left join T_SampleRegist_CheckModeuleItem scmi on scmi.SampleRegistId = sr.NewSampleNo
          left join stlm_test st on sr.NewSampleNo = st.sample_no and st.check_module_id = scmi.CheckModeuleId
          where e.Id in :entrustIds    
      ) as t 
    where (check_module_id is not null or check_module_id = '')
    order by entrust_id, sample_no

   """, nativeQuery = true)
  List<Tuple> findUnsettledSamples(Set<String> entrustIds);

    /**
     * 查询已支付的
     * @param keys
     * @return
     */
  @Query("""
    select t from TestItem t
    left join SettlementMain m on t.mid = m.id
    where m.saveType = 'PAYED'
    and concat(t.sampleNo, t.checkModuleId) in :keys
""")
  List<TestItem> findSubcontractSettledSamples(Set<String> keys);


    /**
     * 从临时表stlm_test_temp复制数据到stlm_test
     * @param tempId stlm_test_temp的m_id（临时关联ID）
     * @param mainId stlm_test的m_id（实际主表ID）
     */
    @Modifying
    @Query(value = """
        INSERT INTO stlm_test (id, m_id, entrust_id, sample_no, check_module_id, check_module_name, sample_unit, price_, old_sample_no, well_no)
        SELECT NEWID(), :mainId, entrust_id, sample_no, check_module_id, check_module_name, sample_unit, price_, old_sample_no, well_no
        FROM stlm_test_temp
        WHERE m_id = :tempId
        """, nativeQuery = true)
    void insertByStlmTestTemp(@Param("tempId") String tempId, @Param("mainId") String mainId);

    @Query(value = """
    select :tempId as tempId, 
           a.sample_no as tempSampleNo, 
           a.check_module_id as tempCheckModuleId, 
           a.check_module_name as tempCheckModuleName,
           b.sample_no as sampleNo, 
           b.check_module_id as checkModuleId,
           b.check_module_name as checkModuleName,
           b.id as testItemId,
           a.sample_no + '_' + a.check_module_id as tempKey,
           b.sample_no + '_' + b.check_module_id as key,
           main.id as mid
    from stlm_test_temp a 
    left join stlm_test b on a.sample_no = b.sample_no and a.check_module_id = b.check_module_id
    left join stlm_main main on main.id = b.mid 
    where a.m_id = :tempId
    and main.save_type <> 'INVALID'
    and main.is_del <> 1
""", nativeQuery = true)
    List<ValidateDuplicatedSampleDTO> checkTempDuplicatedSamples(@Param("tempId") String tempId);

    @Query("""
    select testItem from TestItem testItem left join SettlementMain main on testItem.mid = main.id
    where main.saveType <> 'INVALID'
    and main.isDel = false
""")
    List<TestItem> findSettledSamplesByEntrustIdIn(Collection<String> entrustIds);
}