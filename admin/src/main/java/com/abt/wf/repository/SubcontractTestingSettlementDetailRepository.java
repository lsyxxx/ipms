package com.abt.wf.repository;

import com.abt.wf.entity.SubcontractTestingSettlementDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubcontractTestingSettlementDetailRepository extends JpaRepository<SubcontractTestingSettlementDetail, String> {

    Page<SubcontractTestingSettlementDetail> findByMain_Id(String mainId, Pageable pageable);

    /**
     * 是否存在重复结算的样品
     * @param mid 结算单id
     */
    @Query("""
     select d1
     from SubcontractTestingSettlementDetail d1
     inner join SubcontractTestingSettlementMain m1 on d1.main.id = m1.id
     left join SubcontractTestingSettlementDetail d2 on d1.sampleNo = d2.sampleNo and d1.checkModuleId = d2.checkModuleId and d1.id <> d2.id
     inner join SubcontractTestingSettlementMain m2 on d2.main.id = m2.id
     where (m1.businessState = '已通过' or m1.businessState = '审批中')
     and (m2.businessState = '已通过' or m2.businessState = '审批中')
     and m1.id = :mid
     order by d1.sampleNo, d1.checkModuleId
""")
    List<SubcontractTestingSettlementDetail> findDuplicatedSamplesByMid(String mid);


    @Query("""
     select count(d2) > 0
     from SubcontractTestingSettlementDetail d1
     inner join SubcontractTestingSettlementMain m1 on d1.main.id = m1.id
     left join SubcontractTestingSettlementDetail d2 on d1.sampleNo = d2.sampleNo and d1.checkModuleId = d2.checkModuleId and d1.id <> d2.id
     inner join SubcontractTestingSettlementMain m2 on d2.main.id = m2.id
     where (m1.businessState = '已通过' or m1.businessState = '审批中')
     and (m2.businessState = '已通过' or m2.businessState = '审批中')
     and m1.id = :mid
""")
    boolean duplicatedSamplesExists(String mid);
}