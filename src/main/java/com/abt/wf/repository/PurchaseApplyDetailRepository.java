package com.abt.wf.repository;

import com.abt.wf.entity.PurchaseApplyDetail;
import com.abt.wf.model.PurchaseSummaryAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseApplyDetailRepository extends JpaRepository<PurchaseApplyDetail, String> {


    @Query("""
    select new com.abt.wf.model.PurchaseSummaryAmount(sum(dtl.cost), sum(dtl.currentQuantity), dtl.detailId, dtl.name, dtl.specification, dtl.unit) from PurchaseApplyDetail dtl
    join dtl.main main
    left join MaterialDetail md on dtl.detailId = md.id
    left join MaterialType mt on md.materialTypeId = mt.id
    where main.businessState = '已通过'
    and (:typeName is null or :typeName = '' or mt.name like %:typeName%)
    and (:startDate is null or main.endTime >= :startDate)
    and (:endDate is null or main.endTime <= :endDate)
    group by dtl.detailId, dtl.name, dtl.specification, dtl.unit
""")
    List<PurchaseSummaryAmount> summaryGiftTotalAmount(String typeName, LocalDate startDate, LocalDate endDate);

}