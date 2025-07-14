package com.abt.market.repository;

import com.abt.market.entity.SettlementMain;
import com.abt.market.model.SettlementEntrustDTO;
import com.abt.market.model.SettlementMainListDTO;
import com.abt.market.model.SettlementRequestForm;
import com.abt.sys.model.entity.CustomerInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SettlementMainRepository extends JpaRepository<SettlementMain, String>, JpaSpecificationExecutor<SettlementMain> {

    /**
     * 关联检测项目及其他费用
     *
     * @param id 主表id
     */
    @EntityGraph(attributePaths = {"testItems", "expenseItems", "relations", "summaryTab"})
    @Query("select m from SettlementMain m where m.id = :id")
    SettlementMain findOneWithAllItems(String id);


    long countById(String id);

    /**
     * 根据查询条件查询结算单
     * @param query 模糊查询关键词
     * @param startDate 开始日期
     * @param endDate 结束日期  
     * @param testLike 测试项目模糊查询关键词
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query("""
    select new com.abt.market.model.SettlementMainListDTO(
        m.id,
        m.clientId,
        m.clientName,
        m.companyName,
        m.taxNo,
        m.telephoneNo,
        m.accountBank,
        m.accountNo,
        m.isTax,
        m.taxRate,
        m.discountPercentage,
        m.discountAmount,
        m.totalAmount,
        m.saveType,
        m.remark,
        m.attachments,
        m.createDate,
        m.createUserid,
        m.createUsername,
        m.updateDate,
        m.updateUserid,
        m.updateUsername,
        cast(null as Double),
        cast(null as Boolean)
    )
    from SettlementMain m
    where (:query is null or :query = '' or
           lower(m.id) like lower(concat('%', :query, '%')) or
           lower(m.clientName) like lower(concat('%', :query, '%')) or
           lower(cast(m.totalAmount as string)) like lower(concat('%', :query, '%')))
    and (:startDate is null or m.createDate >= :startDate)
    and (:endDate is null or m.createDate <= :endDate)
    and (:clientId is null or :clientId = '' or m.clientId = :clientId)
    and (:state is null or :state = '' or str(m.saveType) = :state)
    and (:testLike is null or :testLike = '' or m.id in (
        select distinct sm.id from SettlementMain sm 
        left join sm.testItems t 
        where lower(t.entrustId) like lower(concat('%', :testLike, '%')) or
              lower(t.sampleNo) like lower(concat('%', :testLike, '%')) or  
              lower(t.checkModuleName) like lower(concat('%', :testLike, '%'))
    ))
    """)
    Page<SettlementMainListDTO> findMainOnlyByQuery(
        @Param("query") String query,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("testLike") String testLike,
        @Param("clientId") String clientId,
        @Param("state") String  state,
        Pageable pageable);


    @Query("""
       select distinct new CustomerInfo(m.clientId, m.clientId, m.clientName)
       from SettlementMain m
       where m.clientId is not null and m.clientName is not null
       and m.saveType != 'INVALID'
       order by m.clientName
""")
    List<CustomerInfo> getAllCustomers();


    /**
     * 查询结算项目及金额
     */
    @Query(value = """
        select * from stlm_main m 
        left join T_sampleRegist
""", nativeQuery = true)
    List<SettlementEntrustDTO> findEntrustAmount();
}