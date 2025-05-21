package com.abt.wf.repository;

import com.abt.wf.entity.SubcontractTestingSample;
import com.abt.wf.projection.SubcontractTestingSettlementDetailProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubcontractTestingSampleRepository extends JpaRepository<SubcontractTestingSample, String> {
    List<SubcontractTestingSample> findByMid(String mid);

//     @Query("""
//     select new com.abt.wf.entity.SubcontractTestingSettlementDetail(e.entrustId, e.checkModuleName, count(e)) from SubcontractTestingSample e
//     where (e.main.subcontractCompanyName = :subcontractCompanyName)
//     and e.main.createUserid = :userid
//     group by e.entrustId, e.checkModuleName
//     order by e.entrustId, e.checkModuleName
// """)
//     List<SubcontractTestingSettlementDetail> findDetailsBy(String userid, String subcontractCompanyName);


    @Query(value = """
        select s.entrust_id as entrustId, e.ProjectName as projectName, e.JiaFangCompany as entrustCompany, 
               s.check_module_id as checkModuleId, s.check_module_name as checkModuleName, d.unit_ as unit,
               COALESCE(SUM(d.num_), 0) AS settledCount, COUNT(s.id) as num, (COUNT(s.id)-COALESCE(SUM(d.num_), 0)) as unsettledCount
        from wf_sbct_sample s
        left join wf_sbct_stl_dtl d on d.entrust_id = s.entrust_id and d.check_module_id = s.check_module_id
        left join wf_sbct b on b.id = s.mid 
        left join t_entrust e on s.entrust_id = e.Id
        where b.create_userid = :userid 
        and b.biz_state = '已通过'
        and b.subcontract_company_name = :subcontractCompanyName
        group by s.entrust_id, e.ProjectName, e.JiaFangCompany, s.check_module_id, s.check_module_name, d.unit_
        order by s.entrust_id, e.ProjectName, e.JiaFangCompany, s.check_module_id, s.check_module_name, d.unit_
""",
            nativeQuery = true
    )
    List<SubcontractTestingSettlementDetailProjection> findDetailsBy(String userid, String subcontractCompanyName);


}