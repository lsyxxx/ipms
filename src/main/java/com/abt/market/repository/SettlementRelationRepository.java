package com.abt.market.repository;

import com.abt.finance.entity.Invoice;
import com.abt.market.entity.SettlementRelation;
import com.abt.market.model.SettlementRelationType;
import com.abt.wf.entity.InvoiceApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface SettlementRelationRepository extends JpaRepository<SettlementRelation, String> {
    List<SettlementRelation> findByMid(String mid);

    void deleteByMid(String mid);

    void deleteByMidAndBizType(String mid, SettlementRelationType bizType);

    void deleteByMidAndRid(String mid, String rid);

    List<SettlementRelation> findByMidAndBizType(String mid, SettlementRelationType bizType);

    /**
     * 查询关联的invoiceApply
     *
     * @param mid 结算单id
     */
    @Query("""
            select i.invoiceAmount
            from SettlementRelation r
            left join InvoiceApply i on r.rid = i.id
            where r.mid= :mid
            """)
    List<Double> findInvoiceApplyByMid(String mid);

    List<SettlementRelation> findByIdIn(Collection<String> ids);

    List<SettlementRelation> findByBizTypeAndIdIn(SettlementRelationType bizType, Collection<String> ids);
}