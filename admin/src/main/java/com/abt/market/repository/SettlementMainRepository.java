package com.abt.market.repository;

import com.abt.market.entity.SettlementMain;
import com.abt.market.model.*;
import com.abt.sys.model.entity.CustomerInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    and m.isDel = false
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


    @Modifying
    @Query("update SettlementMain set saveType = :saveType where id = :id")
    void updateSaveType(SaveType saveType, String id);


    @Modifying
    @Query("update SettlementMain set isDel = true where id = :id")
    void logicDelete(String id);


    @Query("""
    select new com.abt.market.model.SettlementAgreementDTO(main.id, main.totalAmount, main.clientName, main.remark, main.createUsername, main.createDate, main.saveType) 
    from SettlementMain main 
    left join SettlementRelation rel on main.id = rel.mid
    left join SaleAgreement sale on sale.id = rel.rid
    where rel.bizType = 'AGREEMENT'
    and main.saveType in :saveTypes
    and sale.code = :contractNo
""")
    List<SettlementAgreementDTO> findSettlementAgreementDTOListByContractNo(String contractNo, List<SaveType> saveTypes);

    /**
     * 按项目编号汇总结算情况。
     */
    @Query("""
    select new com.abt.market.model.SettlementStatDTO(
        :entrustId,
        cast(null as string),
        cast(null as string),
        cast(null as string),
        count(m.id),
        coalesce(sum(m.totalAmount), 0)
    )
    from SettlementMain m
    where m.isDel = false
      and m.saveType in :saveTypes
      and exists (
        select s.id from SettlementSummary s
        where s.mid = m.id and s.entrustId = :entrustId
      )
    """)
    SettlementStatDTO findSettlementStatsByEntrustId(String entrustId, List<SaveType> saveTypes);

    /**
     * 按客户id汇总结算情况。
     */
    @Query("""
    select new com.abt.market.model.SettlementStatDTO(
        cast(null as string),
        :clientId,
        max(m.clientName),
        cast(null as string),
        count(m.id),
        coalesce(sum(m.totalAmount), 0)
    )
    from SettlementMain m
    where m.isDel = false
      and m.saveType in :saveTypes
      and m.clientId = :clientId
    """)
    SettlementStatDTO findSettlementStatsByClientId(String clientId, List<SaveType> saveTypes);

    /**
     * 按客户名称汇总结算情况。
     */
    @Query("""
    select new com.abt.market.model.SettlementStatDTO(
        cast(null as string),
        max(m.clientId),
        :clientName,
        cast(null as string),
        count(m.id),
        coalesce(sum(m.totalAmount), 0)
    )
    from SettlementMain m
    where m.isDel = false
      and m.saveType in :saveTypes
      and m.clientName = :clientName
    """)
    SettlementStatDTO findSettlementStatsByClientName(String clientName, List<SaveType> saveTypes);

    /**
     * 按合同编号汇总结算情况，仅统计已绑定销售合同的结算单。
     */
    @Query("""
    select new com.abt.market.model.SettlementStatDTO(
        cast(null as string),
        cast(null as string),
        cast(null as string),
        :contractNo,
        count(m.id),
        coalesce(sum(m.totalAmount), 0)
    )
    from SettlementMain m
    where m.isDel = false
      and m.saveType in :saveTypes
      and exists (
        select rel.id from SettlementRelation rel, SaleAgreement sale
        where rel.mid = m.id
          and rel.bizType = com.abt.market.model.SettlementRelationType.AGREEMENT
          and sale.id = rel.rid
          and sale.code = :contractNo
      )
    """)
    SettlementStatDTO findSettlementStatsByContractNo(String contractNo, List<SaveType> saveTypes);

    /**
     * 按项目分页查询结算情况。
     */
    @Query(value = """
    select new com.abt.market.model.SettlementStatDTO(
        e.id,
        cast(null as string),
        e.jiaFangCompany,
        cast(null as string),
        e.projectName,
        cast(null as string),
        (select count(m.id)
         from SettlementMain m
         where m.isDel = false
           and m.saveType in :saveTypes
           and exists (
             select s.id from SettlementSummary s
             where s.mid = m.id and s.entrustId = e.id
           )),
        (select sum(m.totalAmount)
         from SettlementMain m
         where m.isDel = false
           and m.saveType in :saveTypes
           and exists (
             select s.id from SettlementSummary s
             where s.mid = m.id and s.entrustId = e.id
           ))
    )
    from Entrust e
    where (:entrustId is null or :entrustId = '' or lower(e.id) like lower(concat('%', :entrustId, '%')))
      and (
        :settlementStatus is null
        or :settlementStatus = ''
        or (:settlementStatus = 'SETTLED' and
            (select count(m.id)
             from SettlementMain m
             where m.isDel = false
               and m.saveType in :saveTypes
               and exists (
                 select s.id from SettlementSummary s
                 where s.mid = m.id and s.entrustId = e.id
               )) > 0)
        or (:settlementStatus = 'UNSETTLED' and
            (select count(m.id)
             from SettlementMain m
             where m.isDel = false
               and m.saveType in :saveTypes
               and exists (
                 select s.id from SettlementSummary s
                 where s.mid = m.id and s.entrustId = e.id
               )) = 0)
      )
    """,
    countQuery = """
    select count(e.id)
    from Entrust e
    where (:entrustId is null or :entrustId = '' or lower(e.id) like lower(concat('%', :entrustId, '%')))
      and (
        :settlementStatus is null
        or :settlementStatus = ''
        or (:settlementStatus = 'SETTLED' and
            (select count(m.id)
             from SettlementMain m
             where m.isDel = false
               and m.saveType in :saveTypes
               and exists (
                 select s.id from SettlementSummary s
                 where s.mid = m.id and s.entrustId = e.id
               )) > 0)
        or (:settlementStatus = 'UNSETTLED' and
            (select count(m.id)
             from SettlementMain m
             where m.isDel = false
               and m.saveType in :saveTypes
               and exists (
                 select s.id from SettlementSummary s
                 where s.mid = m.id and s.entrustId = e.id
               )) = 0)
      )
    """)
    Page<SettlementStatDTO> findEntrustStatsPage(String entrustId, String settlementStatus, List<SaveType> saveTypes, Pageable pageable);

    /**
     * 按客户分页查询结算情况。
     */
    @Query(value = """
    select new com.abt.market.model.SettlementStatDTO(
        cast(null as string),
        m.clientId,
        m.clientName,
        cast(null as string),
        cast(null as string),
        cast(null as string),
        count(m.id),
        sum(m.totalAmount)
    )
    from SettlementMain m
    where m.isDel = false
      and m.saveType in :saveTypes
      and (:clientName is null or :clientName = '' or lower(m.clientName) like lower(concat('%', :clientName, '%')))
      and (:settlementStatus is null or :settlementStatus = '' or :settlementStatus = 'SETTLED')
      and (:settlementStatus is null or :settlementStatus = '' or :settlementStatus <> 'UNSETTLED')
    group by m.clientId, m.clientName
    """,
    countQuery = """
    select count(distinct concat(coalesce(m.clientId, ''), '|', coalesce(m.clientName, '')))
    from SettlementMain m
    where m.isDel = false
      and m.saveType in :saveTypes
      and (:clientName is null or :clientName = '' or lower(m.clientName) like lower(concat('%', :clientName, '%')))
      and (:settlementStatus is null or :settlementStatus = '' or :settlementStatus = 'SETTLED')
      and (:settlementStatus is null or :settlementStatus = '' or :settlementStatus <> 'UNSETTLED')
    """)
    Page<SettlementStatDTO> findClientStatsPage(String clientName, String settlementStatus, List<SaveType> saveTypes, Pageable pageable);

    /**
     * 按合同分页查询结算情况。
     */
    @Query(value = """
    select new com.abt.market.model.SettlementStatDTO(
        cast(null as string),
        cast(null as string),
        a.partyA,
        a.code,
        cast(null as string),
        a.name,
        (select count(m.id)
         from SettlementMain m
         where m.isDel = false
           and m.saveType in :saveTypes
           and exists (
             select rel.id
             from SettlementRelation rel
             where rel.mid = m.id
               and rel.bizType = com.abt.market.model.SettlementRelationType.AGREEMENT
               and rel.rid = a.id
           )),
        (select sum(m.totalAmount)
         from SettlementMain m
         where m.isDel = false
           and m.saveType in :saveTypes
           and exists (
             select rel.id
             from SettlementRelation rel
             where rel.mid = m.id
               and rel.bizType = com.abt.market.model.SettlementRelationType.AGREEMENT
               and rel.rid = a.id
           ))
    )
    from SaleAgreement a
    where (:contractQuery is null or :contractQuery = ''
           or lower(a.code) like lower(concat('%', :contractQuery, '%'))
           or lower(a.name) like lower(concat('%', :contractQuery, '%')))
      and (
        :settlementStatus is null
        or :settlementStatus = ''
        or (:settlementStatus = 'SETTLED' and
            (select count(m.id)
             from SettlementMain m
             where m.isDel = false
               and m.saveType in :saveTypes
               and exists (
                 select rel.id
                 from SettlementRelation rel
                 where rel.mid = m.id
                   and rel.bizType = com.abt.market.model.SettlementRelationType.AGREEMENT
                   and rel.rid = a.id
               )) > 0)
        or (:settlementStatus = 'UNSETTLED' and
            (select count(m.id)
             from SettlementMain m
             where m.isDel = false
               and m.saveType in :saveTypes
               and exists (
                 select rel.id
                 from SettlementRelation rel
                 where rel.mid = m.id
                   and rel.bizType = com.abt.market.model.SettlementRelationType.AGREEMENT
                   and rel.rid = a.id
               )) = 0)
      )
    """,
    countQuery = """
    select count(a.id)
    from SaleAgreement a
    where (:contractQuery is null or :contractQuery = ''
           or lower(a.code) like lower(concat('%', :contractQuery, '%'))
           or lower(a.name) like lower(concat('%', :contractQuery, '%')))
      and (
        :settlementStatus is null
        or :settlementStatus = ''
        or (:settlementStatus = 'SETTLED' and
            (select count(m.id)
             from SettlementMain m
             where m.isDel = false
               and m.saveType in :saveTypes
               and exists (
                 select rel.id
                 from SettlementRelation rel
                 where rel.mid = m.id
                   and rel.bizType = com.abt.market.model.SettlementRelationType.AGREEMENT
                   and rel.rid = a.id
               )) > 0)
        or (:settlementStatus = 'UNSETTLED' and
            (select count(m.id)
             from SettlementMain m
             where m.isDel = false
               and m.saveType in :saveTypes
               and exists (
                 select rel.id
                 from SettlementRelation rel
                 where rel.mid = m.id
                   and rel.bizType = com.abt.market.model.SettlementRelationType.AGREEMENT
                   and rel.rid = a.id
               )) = 0)
      )
    """)
    Page<SettlementStatDTO> findContractStatsPage(String contractQuery, String settlementStatus, List<SaveType> saveTypes, Pageable pageable);

    /**
     * 查询指定项目编号关联的结算单分页列表。
     */
    @Query("""
    select m
    from SettlementMain m
    where m.isDel = false
      and m.saveType in :saveTypes
      and exists (
        select s.id from SettlementSummary s
        where s.mid = m.id and s.entrustId = :entrustId
      )
    """)
    Page<SettlementMain> findDetailPageByEntrustId(String entrustId, List<SaveType> saveTypes, Pageable pageable);

    /**
     * 按项目编号查询关联结算单分页列表。
     */
    @Query("""
    select new com.abt.market.model.SettlementAgreementDTO(
        main.id,
        main.totalAmount,
        main.clientName,
        main.remark,
        main.createUsername,
        main.createDate,
        main.saveType
    )
    from SettlementMain main
    where main.isDel = false
      and main.saveType in :saveTypes
      and exists (
        select s.id from SettlementSummary s
        where s.mid = main.id and s.entrustId = :entrustId
      )
    """)
    Page<SettlementAgreementDTO> findSettlementAgreementPageByEntrustId(String entrustId, List<SaveType> saveTypes, Pageable pageable);

    /**
     * 查询指定客户关联的结算单分页列表。
     */
    @Query("""
    select m
    from SettlementMain m
    where m.isDel = false
      and m.saveType in :saveTypes
      and m.clientId = :clientId
    """)
    Page<SettlementMain> findDetailPageByClientId(String clientId, List<SaveType> saveTypes, Pageable pageable);

    @Query("""
    select m
    from SettlementMain m
    where m.isDel = false
      and m.saveType <> com.abt.market.model.SaveType.TEMP
      and m.createDate >= :startTime
      and m.createDate < :endTime
    order by m.createDate asc, m.id asc
    """)
    List<SettlementMain> findExportSettlements(LocalDateTime startTime, LocalDateTime endTime);

}
