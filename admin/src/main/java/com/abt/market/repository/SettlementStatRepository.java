package com.abt.market.repository;

import com.abt.market.projection.EntrustSettlementDiffProjection;
import com.abt.market.projection.EntrustSettlementExportProjection;
import com.abt.market.projection.EntrustSampleSettlementExportProjection;
import com.abt.market.projection.EntrustSettlementStatProjection;
import com.abt.market.projection.SettlementYearSummaryProjection;
import com.abt.testing.entity.Entrust;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 结算统计查询。
 */
public interface SettlementStatRepository extends JpaRepository<Entrust, String> {

    @Query(value = """
    with stats as (
        select
            e.Id as entrustId,
            e.ProjectName as projectName,
            e.JiaFangCompany as clientName,
            (
                select count(1)
                from T_SampleRegist_CheckModeuleItem scmi
                where scmi.entrustId = e.Id
            ) as totalCount,
            (
                select count(1)
                from T_SampleRegist_CheckModeuleItem scmi
                where scmi.entrustId = e.Id
                  and exists (
                      select 1
                      from stlm_test ti
                      inner join stlm_main sm on sm.id = ti.m_id
                      where sm.is_del = 0
                        and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
                        and ti.entrust_id = scmi.entrustId
                        and ti.sample_no = scmi.SampleRegistId
                        and ti.check_module_id = scmi.CheckModeuleId
                  )
            ) as settledCount,
            (
                select count(1)
                from stlm_test ti
                inner join stlm_main sm on sm.id = ti.m_id
                where sm.is_del = 0
                  and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
                  and ti.entrust_id = e.Id
                  and not exists (
                      select 1
                      from T_SampleRegist_CheckModeuleItem scmi
                      where scmi.entrustId = ti.entrust_id
                        and scmi.SampleRegistId = ti.sample_no
                        and scmi.CheckModeuleId = ti.check_module_id
                  )
            ) as extraSettledCount,
            (
                select coalesce(sum(ss.amt_), 0)
                from stlm_smry ss
                inner join stlm_main sm on sm.id = ss.m_id
                where sm.is_del = 0
                  and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
                  and ss.entrust_id = e.Id
            ) as settledAmount
        from T_entrust e
        where (:entrustId is null or :entrustId = '' or lower(e.Id) like lower(concat('%', :entrustId, '%')))
          and (:projectName is null or :projectName = '' or lower(e.ProjectName) like lower(concat('%', :projectName, '%')))
          and (:clientName is null or :clientName = '' or lower(e.JiaFangCompany) like lower(concat('%', :clientName, '%')))
          and (:startTime is null or e.CreateDate >= :startTime)
          and (:endTime is null or e.CreateDate < :endTime)
    )
    select
        s.entrustId as entrustId,
        s.projectName as projectName,
        s.clientName as clientName,
        cast(s.totalCount as bigint) as totalCount,
        cast(s.settledCount as bigint) as settledCount,
        cast(case when s.totalCount - s.settledCount > 0 then s.totalCount - s.settledCount else 0 end as bigint) as unsettledCount,
        cast(s.extraSettledCount as bigint) as extraSettledCount,
        cast(s.settledAmount as decimal(18, 2)) as settledAmount,
        case
            when s.totalCount > 0 and s.settledCount >= s.totalCount then 'SETTLED'
            when s.settledCount = 0 and s.extraSettledCount = 0 then 'UNSETTLED'
            else 'PARTIALLY_SETTLED'
        end as settlementStatus
    from stats s
    where (
        :settlementStatus is null
        or :settlementStatus = ''
        or (
            case
                when s.totalCount > 0 and s.settledCount >= s.totalCount then 'SETTLED'
                when s.settledCount = 0 and s.extraSettledCount = 0 then 'UNSETTLED'
                else 'PARTIALLY_SETTLED'
            end = :settlementStatus
        )
    )
    order by s.entrustId desc
    """,
    countQuery = """
    with stats as (
        select
            e.Id as entrustId,
            (
                select count(1)
                from T_SampleRegist_CheckModeuleItem scmi
                where scmi.entrustId = e.Id
            ) as totalCount,
            (
                select count(1)
                from T_SampleRegist_CheckModeuleItem scmi
                where scmi.entrustId = e.Id
                  and exists (
                      select 1
                      from stlm_test ti
                      inner join stlm_main sm on sm.id = ti.m_id
                      where sm.is_del = 0
                        and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
                        and ti.entrust_id = scmi.entrustId
                        and ti.sample_no = scmi.SampleRegistId
                        and ti.check_module_id = scmi.CheckModeuleId
                  )
            ) as settledCount,
            (
                select count(1)
                from stlm_test ti
                inner join stlm_main sm on sm.id = ti.m_id
                where sm.is_del = 0
                  and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
                  and ti.entrust_id = e.Id
                  and not exists (
                      select 1
                      from T_SampleRegist_CheckModeuleItem scmi
                      where scmi.entrustId = ti.entrust_id
                        and scmi.SampleRegistId = ti.sample_no
                        and scmi.CheckModeuleId = ti.check_module_id
                  )
            ) as extraSettledCount
        from T_entrust e
        where (:entrustId is null or :entrustId = '' or lower(e.Id) like lower(concat('%', :entrustId, '%')))
          and (:projectName is null or :projectName = '' or lower(e.ProjectName) like lower(concat('%', :projectName, '%')))
          and (:clientName is null or :clientName = '' or lower(e.JiaFangCompany) like lower(concat('%', :clientName, '%')))
          and (:startTime is null or e.CreateDate >= :startTime)
          and (:endTime is null or e.CreateDate < :endTime)
    )
    select count(1)
    from stats s
    where (
        :settlementStatus is null
        or :settlementStatus = ''
        or (
            case
                when s.totalCount > 0 and s.settledCount >= s.totalCount then 'SETTLED'
                when s.settledCount = 0 and s.extraSettledCount = 0 then 'UNSETTLED'
                else 'PARTIALLY_SETTLED'
            end = :settlementStatus
        )
    )
    """, nativeQuery = true)
    Page<EntrustSettlementStatProjection> findEntrustStatsPage(@Param("entrustId") String entrustId,
                                                               @Param("projectName") String projectName,
                                                               @Param("clientName") String clientName,
                                                               @Param("startTime") LocalDateTime startTime,
                                                               @Param("endTime") LocalDateTime endTime,
                                                               @Param("settlementStatus") String settlementStatus,
                                                               Pageable pageable);

    @Query(value = """
    with stats as (
        select
            e.Id as entrustId,
            e.ProjectName as projectName,
            e.JiaFangCompany as clientName,
            (
                select count(1)
                from T_SampleRegist_CheckModeuleItem scmi
                where scmi.entrustId = e.Id
            ) as totalCount,
            (
                select count(1)
                from T_SampleRegist_CheckModeuleItem scmi
                where scmi.entrustId = e.Id
                  and exists (
                      select 1
                      from stlm_test ti
                      inner join stlm_main sm on sm.id = ti.m_id
                      where sm.is_del = 0
                        and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
                        and ti.entrust_id = scmi.entrustId
                        and ti.sample_no = scmi.SampleRegistId
                        and ti.check_module_id = scmi.CheckModeuleId
                  )
            ) as settledCount,
            (
                select count(1)
                from stlm_test ti
                inner join stlm_main sm on sm.id = ti.m_id
                where sm.is_del = 0
                  and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
                  and ti.entrust_id = e.Id
                  and not exists (
                      select 1
                      from T_SampleRegist_CheckModeuleItem scmi
                      where scmi.entrustId = ti.entrust_id
                        and scmi.SampleRegistId = ti.sample_no
                        and scmi.CheckModeuleId = ti.check_module_id
                  )
            ) as extraSettledCount,
            (
                select coalesce(sum(ss.amt_), 0)
                from stlm_smry ss
                inner join stlm_main sm on sm.id = ss.m_id
                where sm.is_del = 0
                  and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
                  and ss.entrust_id = e.Id
            ) as settledAmount
        from T_entrust e
        where e.Id = :entrustId
    )
    select
        s.entrustId as entrustId,
        s.projectName as projectName,
        s.clientName as clientName,
        cast(s.totalCount as bigint) as totalCount,
        cast(s.settledCount as bigint) as settledCount,
        cast(case when s.totalCount - s.settledCount > 0 then s.totalCount - s.settledCount else 0 end as bigint) as unsettledCount,
        cast(s.extraSettledCount as bigint) as extraSettledCount,
        cast(s.settledAmount as decimal(18, 2)) as settledAmount,
        case
            when s.totalCount > 0 and s.settledCount >= s.totalCount then 'SETTLED'
            when s.settledCount = 0 and s.extraSettledCount = 0 then 'UNSETTLED'
            else 'PARTIALLY_SETTLED'
        end as settlementStatus
    from stats s
    """, nativeQuery = true)
    EntrustSettlementStatProjection findEntrustStat(@Param("entrustId") String entrustId);

    @Query(value = """
    with src as (
        select
            scmi.Id as sourceId,
            scmi.entrustId as entrustId,
            scmi.SampleRegistId as sampleRegistId,
            scmi.CheckModeuleId as checkModeuleId,
            scmi.CheckModeuleName as checkModeuleName
        from T_SampleRegist_CheckModeuleItem scmi
        where scmi.entrustId = :entrustId
    ),
    stl as (
        select
            ti.id as testItemId,
            ti.entrust_id as entrustId,
            ti.sample_no as sampleNo,
            ti.check_module_id as checkModeuleId,
            ti.check_module_name as checkModeuleName,
            sm.id as settlementMainId,
            sm.save_type as settlementSaveType,
            sm.create_date as settlementCreateDate,
            sm.client_name as clientName
        from stlm_test ti
        inner join stlm_main sm on sm.id = ti.m_id
        where ti.entrust_id = :entrustId
          and sm.is_del = 0
          and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
    )
    select
        case
            when src.sourceId is not null and stl.testItemId is not null then 'MATCHED'
            when src.sourceId is not null then 'UNSETTLED'
            else 'EXTRA_SETTLED'
        end as diffType,
        coalesce(src.entrustId, stl.entrustId) as entrustId,
        src.sampleRegistId as sampleRegistId,
        stl.sampleNo as sampleNo,
        coalesce(src.checkModeuleId, stl.checkModeuleId) as checkModeuleId,
        coalesce(src.checkModeuleName, stl.checkModeuleName) as checkModeuleName,
        stl.testItemId as testItemId,
        stl.settlementMainId as settlementMainId,
        stl.settlementSaveType as settlementSaveType,
        stl.settlementCreateDate as settlementCreateDate,
        stl.clientName as clientName
    from src
    full outer join stl
      on src.entrustId = stl.entrustId
     and src.sampleRegistId = stl.sampleNo
     and src.checkModeuleId = stl.checkModeuleId
    order by
        case
            when src.sourceId is not null and stl.testItemId is not null then 1
            when src.sourceId is not null then 2
            else 3
        end,
        coalesce(src.sampleRegistId, stl.sampleNo),
        coalesce(src.checkModeuleId, stl.checkModeuleId),
        stl.settlementCreateDate
    """, nativeQuery = true)
    List<EntrustSettlementDiffProjection> findEntrustDiffItems(@Param("entrustId") String entrustId);

    @Query(value = """
    with stats as (
        select
            e.Id as entrustId,
            (
                select count(1)
                from T_SampleRegist_CheckModeuleItem scmi
                where scmi.entrustId = e.Id
            ) as totalCount,
            (
                select count(1)
                from T_SampleRegist_CheckModeuleItem scmi
                where scmi.entrustId = e.Id
                  and exists (
                      select 1
                      from stlm_test ti
                      inner join stlm_main sm on sm.id = ti.m_id
                      where sm.is_del = 0
                        and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
                        and ti.entrust_id = scmi.entrustId
                        and ti.sample_no = scmi.SampleRegistId
                        and ti.check_module_id = scmi.CheckModeuleId
                  )
            ) as settledCount,
            (
                select count(1)
                from stlm_test ti
                inner join stlm_main sm on sm.id = ti.m_id
                where sm.is_del = 0
                  and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
                  and ti.entrust_id = e.Id
                  and not exists (
                      select 1
                      from T_SampleRegist_CheckModeuleItem scmi
                      where scmi.entrustId = ti.entrust_id
                        and scmi.SampleRegistId = ti.sample_no
                        and scmi.CheckModeuleId = ti.check_module_id
                  )
            ) as extraSettledCount,
            (
                select coalesce(sum(ss.amt_), 0)
                from stlm_smry ss
                inner join stlm_main sm on sm.id = ss.m_id
                where sm.is_del = 0
                  and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
                  and ss.entrust_id = e.Id
            ) as settledAmount
        from T_entrust e
        where (:startTime is null or e.CreateDate >= :startTime)
          and (:endTime is null or e.CreateDate < :endTime)
    )
    select
        cast(null as int) as year,
        cast(sum(case when s.totalCount > 0 and s.settledCount >= s.totalCount then 1 else 0 end) as bigint) as settledEntrustCount,
        cast(sum(case when (s.totalCount > 0 and s.settledCount < s.totalCount and s.settledCount > 0) or (s.totalCount = 0 and s.extraSettledCount > 0) then 1 else 0 end) as bigint) as partialSettledEntrustCount,
        cast(sum(case when s.settledCount = 0 and s.extraSettledCount = 0 then 1 else 0 end) as bigint) as unsettledEntrustCount,
        cast(coalesce(sum(s.settledAmount), 0) as decimal(18, 2)) as settledAmount
    from stats s
    """, nativeQuery = true)
    SettlementYearSummaryProjection findSummary(@Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime);

    @Query(value = """
    select x.id
    from (
        select sm.id, sm.create_date
        from stlm_main sm
        inner join stlm_smry ss on ss.m_id = sm.id
        where sm.is_del = 0
          and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
          and ss.entrust_id = :entrustId
        union
        select sm.id, sm.create_date
        from stlm_main sm
        inner join stlm_test ti on ti.m_id = sm.id
        where sm.is_del = 0
          and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
          and ti.entrust_id = :entrustId
    ) x
    order by x.create_date, x.id
    """, nativeQuery = true)
    List<String> findSettlementIdsByEntrustId(@Param("entrustId") String entrustId);

    @Query(value = """
    with stats as (
        select
            e.Id as entrustId,
            e.HtBianHao as contractNo,
            e.JiaFangCompany as clientName,
            e.ProjectName as projectName,
            (
                select count(1)
                from T_SampleRegist_CheckModeuleItem scmi
                where scmi.entrustId = e.Id
            ) as totalCount,
            (
                select count(1)
                from T_SampleRegist_CheckModeuleItem scmi
                where scmi.entrustId = e.Id
                  and exists (
                      select 1
                      from stlm_test ti
                      inner join stlm_main sm on sm.id = ti.m_id
                      where sm.is_del = 0
                        and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
                        and ti.entrust_id = scmi.entrustId
                        and ti.sample_no = scmi.SampleRegistId
                        and ti.check_module_id = scmi.CheckModeuleId
                  )
            ) as settledCount,
            (
                select count(1)
                from stlm_test ti
                inner join stlm_main sm on sm.id = ti.m_id
                where sm.is_del = 0
                  and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
                  and ti.entrust_id = e.Id
                  and not exists (
                      select 1
                      from T_SampleRegist_CheckModeuleItem scmi
                      where scmi.entrustId = ti.entrust_id
                        and scmi.SampleRegistId = ti.sample_no
                        and scmi.CheckModeuleId = ti.check_module_id
                  )
            ) as extraSettledCount,
            (
                select coalesce(sum(ss.amt_), 0)
                from stlm_smry ss
                inner join stlm_main sm on sm.id = ss.m_id
                where sm.is_del = 0
                  and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
                  and ss.entrust_id = e.Id
            ) as settledAmount,
            (
                select string_agg(convert(varchar(128), x.id), ',')
                from (
                    select sm.id
                    from stlm_main sm
                    inner join stlm_smry ss on ss.m_id = sm.id
                    where sm.is_del = 0
                      and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
                      and ss.entrust_id = e.Id
                    union
                    select sm.id
                    from stlm_main sm
                    inner join stlm_test ti on ti.m_id = sm.id
                    where sm.is_del = 0
                      and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
                      and ti.entrust_id = e.Id
                ) x
            ) as settlementIds
        from T_entrust e
        where e.HtBianHao like concat('%', :yearText, '%')
    )
    select
        s.entrustId as entrustId,
        s.contractNo as contractNo,
        s.clientName as clientName,
        s.projectName as projectName,
        cast(s.totalCount as bigint) as totalCount,
        cast(s.settledCount as bigint) as settledCount,
        cast(s.extraSettledCount as bigint) as extraSettledCount,
        cast(s.settledAmount as decimal(18, 2)) as settledAmount,
        s.settlementIds as settlementIds
    from stats s
    order by s.entrustId
    """, nativeQuery = true)
    List<EntrustSettlementExportProjection> findEntrustExportRowsByContractYear(@Param("yearText") String yearText);

    @Query(value = """
    select
        e.Id as entrustId,
        e.HtBianHao as contractNo,
        e.JiaFangCompany as clientName,
        e.ProjectName as projectName,
        scmi.SampleRegistId as sampleRegistId,
        scmi.CheckModeuleId as checkModeuleId,
        scmi.CheckModeuleName as checkModeuleName,
        case
            when exists (
                select 1
                from stlm_test ti
                inner join stlm_main sm on sm.id = ti.m_id
                where sm.is_del = 0
                  and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
                  and ti.entrust_id = scmi.entrustId
                  and ti.sample_no = scmi.SampleRegistId
                  and ti.check_module_id = scmi.CheckModeuleId
            ) then '已结算'
            else '未结算'
        end as settledStatus,
        (
            select string_agg(convert(varchar(128), sm.id), ',')
            from stlm_test ti
            inner join stlm_main sm on sm.id = ti.m_id
            where sm.is_del = 0
              and sm.save_type in ('SAVE', 'INVOICING', 'INVOICE', 'PAYED')
              and ti.entrust_id = scmi.entrustId
              and ti.sample_no = scmi.SampleRegistId
              and ti.check_module_id = scmi.CheckModeuleId
        ) as settlementIds
    from T_entrust e
    inner join T_SampleRegist_CheckModeuleItem scmi on scmi.entrustId = e.Id
    where e.HtBianHao like concat('%', :yearText, '%')
    order by e.Id, scmi.SampleRegistId, scmi.CheckModeuleId
    """, nativeQuery = true)
    List<EntrustSampleSettlementExportProjection> findEntrustSampleSettlementRows(@Param("yearText") String yearText);
}
