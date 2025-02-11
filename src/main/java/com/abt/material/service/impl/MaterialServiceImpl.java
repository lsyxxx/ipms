package com.abt.material.service.impl;

import com.abt.common.util.QueryUtil;
import com.abt.material.entity.MaterialDetail;
import com.abt.material.model.MaterialRequestForm;
import com.abt.material.repository.MaterialDetailRepository;
import com.abt.material.service.MaterialService;
import com.abt.sys.exception.BusinessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 物品耗材
 */
@Service
@Slf4j
public class MaterialServiceImpl implements MaterialService {

    private final MaterialDetailRepository materialDetailRepository;

    @PersistenceContext
    private EntityManager entityManager;


    public MaterialServiceImpl(MaterialDetailRepository materialDetailRepository) {
        this.materialDetailRepository = materialDetailRepository;
    }


    @Override
    public List<MaterialDetail> findAll() {
        return materialDetailRepository.findAll(
                Sort.by(
                        Sort.Order.asc("deptName"),
                        Sort.Order.asc("index"),
                        Sort.Order.asc("name"),
                        Sort.Order.asc("specification")
                ));
    }

    @Override
    public Page<MaterialDetail> findByDeptPageable(MaterialRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(),
                Sort.by(Sort.Order.asc("deptId"), Sort.Order.asc("name"), Sort.Order.asc("index")));
        return materialDetailRepository.findByQueryPageable(requestForm.getDeptId(), pageable);
    }

    @Override
    public Page<MaterialDetail> findByQueryPageable(MaterialRequestForm requestForm) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<MaterialDetail> selectQuery = criteriaBuilder.createQuery(MaterialDetail.class);
        Root<MaterialDetail> root = selectQuery.from(MaterialDetail.class);
        //where
        List<Predicate> predicates = new ArrayList<>();
        if (requestForm.getProperties() != null && !requestForm.getProperties().isEmpty()) {
            predicates = QueryUtil.likeQueryPredicates(requestForm.getQuery(), requestForm.getProperties(), criteriaBuilder, root);
        }
        selectQuery.where(criteriaBuilder.or(predicates.toArray(new Predicate[0])));
        selectQuery.orderBy(QueryUtil.orderBy(requestForm.getOrderBy(), criteriaBuilder, root, true));
        //分页
        final TypedQuery<MaterialDetail> createQuery = entityManager.createQuery(selectQuery);
        createQuery.setFirstResult(requestForm.getFirstResult());
        createQuery.setMaxResults(requestForm.getLimit());
        //count

        return new PageImpl<>(createQuery.getResultList());
    }


}
