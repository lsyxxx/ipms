package com.abt.testing.service.impl;

import com.abt.common.util.QueryUtil;
import com.abt.market.entity.SettlementItem;
import com.abt.sys.model.entity.CustomerInfo;
import com.abt.testing.entity.Entrust;
import com.abt.testing.entity.SampleRegistCheckModeuleItem;
import com.abt.testing.model.EntrustRequestForm;
import com.abt.testing.repository.EntrustRepository;
import com.abt.testing.service.EntrustService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 委托单(业务登记)
 */
@Service
public class EntrustServiceImpl implements EntrustService {

    private final EntrustRepository entrustRepository;
    @PersistenceContext
    private EntityManager entityManager;


    public EntrustServiceImpl(EntrustRepository entrustRepository) {
        this.entrustRepository = entrustRepository;
    }

    /**
     * 查询委托单列表
     * @param requestForm 查询条件
     * @return 委托单列表
     */
    @Override
    public List<Entrust> findEntrustListByQuery(EntrustRequestForm requestForm) {
        //build
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        //select *
        CriteriaQuery<Entrust> selectQuery = criteriaBuilder.createQuery(Entrust.class);
        //from
        final Root<Entrust> root = selectQuery.from(Entrust.class);
        //left join
        Join<Entrust, CustomerInfo> customerJoin = root.join("customer", JoinType.LEFT);

        //where
        Predicate where = criteriaBuilder.conjunction();
        if (StringUtils.isNotBlank(requestForm.getQuery())) {
            String queryLike = QueryUtil.like(requestForm.getQuery());
            final Predicate or = criteriaBuilder.or(
                    //委托单编号
                    criteriaBuilder.like(root.get("id"), queryLike),
                    //合同编号
                    criteriaBuilder.like(root.get("htBianHao"), queryLike),
                    //甲方执行单位
                    criteriaBuilder.like(root.get("jiaFangCompany"), queryLike),
                    //客户名称
                    criteriaBuilder.like(customerJoin.get("customerName"), queryLike),
                    //项目
                    criteriaBuilder.like(root.get("projectName"), queryLike)
            );
            where = criteriaBuilder.and(where, or);
        }
        //order by
        selectQuery.orderBy(criteriaBuilder.asc(root.get("id")), criteriaBuilder.asc(root.get("htBianHao")), criteriaBuilder.asc(root.get("customNo")));
        selectQuery.where(where);
        TypedQuery<Entrust> result = entityManager.createQuery(selectQuery);
        return result.getResultList();
    }


    @Override
    public List<SettlementItem> findSampleCheckModules(String entrustNo) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<SettlementItem> selectQuery = criteriaBuilder.createQuery(SettlementItem.class);
        Root<SampleRegistCheckModeuleItem> root = selectQuery.from(SampleRegistCheckModeuleItem.class);
        selectQuery.select(criteriaBuilder.construct(SettlementItem.class, root.get("checkModeuleId"), root.get("checkModeuleName"), criteriaBuilder.count(root)));
        selectQuery.where(criteriaBuilder.equal(root.get("entrustId"), entrustNo));
        selectQuery.groupBy(root.get("checkModeuleId"), root.get("checkModeuleName"));
        selectQuery.orderBy(criteriaBuilder.asc(root.get("checkModeuleId")));
        return entityManager.createQuery(selectQuery).getResultList();
    }
}
