package com.abt.wf.repository;

import com.abt.material.model.PurchaseDetailDTO;
import com.abt.wf.entity.PurchaseApplyDetail;
import com.abt.wf.model.PurchaseSummaryAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseApplyDetailRepository extends JpaRepository<PurchaseApplyDetail, String> {


    @Query("""
    select new com.abt.wf.model.PurchaseSummaryAmount(sum(dtl.cost), sum(dtl.currentQuantity), dtl.detailId, md.name, md.specification, md.unit, dtl.price) from PurchaseApplyDetail dtl
    join dtl.main main
    left join MaterialDetail md on dtl.detailId = md.id
    left join MaterialType mt on md.materialTypeId = mt.id
    where main.businessState = '已通过'
    and (:typeName is null or :typeName = '' or mt.name like %:typeName%)
    and (:startDate is null or CAST(main.endTime AS LocalDate) >= :startDate)
    and (:endDate is null or CAST(main.endTime AS LocalDate) <= :endDate)
    group by dtl.detailId, md.name, md.specification, md.unit, dtl.price
""")
    List<PurchaseSummaryAmount> summaryGiftTotalAmount(String typeName, LocalDate startDate, LocalDate endDate);

    @Query("""
    select sum(dtl.cost)
    from PurchaseApplyDetail dtl
    join dtl.main main
    left join MaterialDetail md on dtl.detailId = md.id
    left join MaterialType mt on md.materialTypeId = mt.id
    where main.businessState = '已通过'
    and (:typeName is null or :typeName = '' or mt.name like %:typeName%)
    and (:startDate is null or CAST(main.endTime AS LocalDate) >= :startDate)
    and (:endDate is null or CAST(main.endTime AS LocalDate) <= :endDate)
""")
    Double sumPurchase(String typeName, LocalDate startDate, LocalDate endDate);


    @Query("""
    select new com.abt.material.model.PurchaseDetailDTO(main.id, mt.id, mt.name, dtl.detailId, dtl.name, dtl.specification, dtl.unit, dtl.price, dtl.quantity, dtl.cost, main.createDate) 
    from PurchaseApplyMain main
    left join PurchaseApplyDetail dtl on main.id = dtl.main.id
    left join MaterialDetail md on dtl.detailId = md.id
    left join MaterialType mt on md.materialTypeId = mt.id
    where main.businessState = '已通过'
    and (:typeName is null or :typeName = '' or mt.name like %:typeName%)
    and (:startDate is null or CAST(main.endTime AS LocalDate) >= :startDate)
    and (:endDate is null or CAST(main.endTime AS LocalDate) <= :endDate)
    order by mt.id, dtl.detailId, main.createDate
    """)
    List<PurchaseDetailDTO> findPurchaseDetailDTOList(String typeName, LocalDate startDate, LocalDate endDate);


}