package com.abt.wf.repository;

import com.abt.wf.entity.SubcontractTestingSample;
import com.abt.wf.model.SbctSummaryData;
import com.abt.wf.projection.SubcontractTestingSettlementDetailProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface SubcontractTestingSampleRepository extends JpaRepository<SubcontractTestingSample, String> {
    List<SubcontractTestingSample> findByMid(String mid);

    @Query(value = """
        select s.entrust_id as entrustId, e.ProjectName as projectName, e.JiaFangCompany as entrustCompany, 
               s.check_module_id as checkModuleId, s.check_module_name as checkModuleName,
               count(d.id) AS settledCount, COUNT(s.id) as num, (COUNT(s.id)-count(d.id)) as unsettledCount
        from wf_sbct_sample s
        left join wf_sbct_stl_dtl d on d.entrust_id = s.entrust_id and d.check_module_id = s.check_module_id
        left join wf_sbct b on b.id = s.mid 
        left join t_entrust e on s.entrust_id = e.Id
        where b.create_userid = :userid 
        and b.biz_state = '已通过'
        and b.subcontract_company_name = :subcontractCompanyName
        group by s.entrust_id, e.ProjectName, e.JiaFangCompany, s.check_module_id, s.check_module_name
        order by s.entrust_id, e.ProjectName, e.JiaFangCompany, s.check_module_id, s.check_module_name
""",
            nativeQuery = true
    )
    List<SubcontractTestingSettlementDetailProjection> findDetailsBy(String userid, String subcontractCompanyName);


    @Query("""
    select b from SubcontractTesting a
    left join SubcontractTestingSample b on a.id = b.mid
    where (a.businessState = '已通过' or a.businessState = '审批中')
    and concat(b.newSampleNo, b.checkModuleId) in :keys
""")
    List<SubcontractTestingSample> findSamplesByKey(Set<String> keys);

    /**
     * 查询与申请单(mids)中样品重复的
     * @param mids 申请单id list
     * @return 仅返回重复的样品的
     */
    @Query("""
    select s2 from SubcontractTestingSample s1
    inner join SubcontractTesting a on a.id = s1.mid
    inner join SubcontractTestingSample s2 on s1.newSampleNo = s2.newSampleNo and s1.checkModuleId = s2.checkModuleId and s1.id <> s2.id
    where (a.businessState = '已通过' or a.businessState = '审批中')
    and a.id in :mids
    order by s1.newSampleNo, s1.checkModuleId
""")
    List<SubcontractTestingSample> findDuplicatedSamples(List<String> mids);


    @Query("""
    select s from SubcontractTestingSample s where s.mid in :mids
""")
    Page<SubcontractTestingSample> findByMids(List<String> mids, Pageable pageable);


    @Query("""
    select new com.abt.wf.model.SbctSummaryData(b.entrustId, b.checkModuleId, b.checkModuleName, count(b))
    from SubcontractTesting a
    left join SubcontractTestingSample b on a.id = b.mid
    where (a.businessState = '已通过' or a.businessState = '审批中')
    and a.id in :mids
    group by b.entrustId, b.checkModuleId, b.checkModuleName
""")
    List<SbctSummaryData> getSummaryData(List<String> mids);
}