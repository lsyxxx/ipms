package com.abt.finance.service.impl;

import com.abt.app.entity.PushRegister;
import com.abt.app.service.PushService;
import com.abt.common.model.User;
import com.abt.common.util.JsonUtil;
import com.abt.common.util.QueryUtil;
import com.abt.finance.entity.ReceivePayment;
import com.abt.finance.entity.ReceivePaymentConfig;
import com.abt.finance.entity.ReceivePaymentReference;
import com.abt.finance.model.ReceivePaymentRequestForm;
import com.abt.finance.repository.ReceivePaymentConfigRepository;
import com.abt.finance.repository.ReceivePaymentRepository;
import com.abt.finance.service.ReceivePaymentService;
import com.abt.market.entity.SaleAgreement;
import com.abt.market.repository.SaleAgreementRepository;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.util.WithQueryUtil;
import com.abt.wf.entity.InvoiceApply;
import com.abt.wf.model.InvoiceApplyStat;
import com.abt.wf.repository.InvoiceApplyRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.abt.finance.config.Constants.REC_PAY_CONFIG_TYPE_NOTIFY;

/**
 *
 */
@Service
public class ReceivePaymentServiceImpl implements ReceivePaymentService {
    private final ReceivePaymentRepository receivePaymentRepository;
    private final ReceivePaymentConfigRepository receivePaymentConfigRepository;
    private final InvoiceApplyRepository invoiceApplyRepository;
    private final SaleAgreementRepository saleAgreementRepository;
    private final PushService pushService;


    @PersistenceContext
    private EntityManager entityManager;


    public ReceivePaymentServiceImpl(ReceivePaymentRepository receivePaymentRepository, ReceivePaymentConfigRepository receivePaymentConfigRepository,
                                     InvoiceApplyRepository invoiceApplyRepository, SaleAgreementRepository saleAgreementRepository, PushService pushService) {
        this.receivePaymentRepository = receivePaymentRepository;
        this.receivePaymentConfigRepository = receivePaymentConfigRepository;
        this.invoiceApplyRepository = invoiceApplyRepository;
        this.saleAgreementRepository = saleAgreementRepository;
        this.pushService = pushService;
    }


    @Override
    public void registerPayment(ReceivePayment receivePayment) {
        //关联的开票申请(receivePaymentReference)在前端组合好
        final List<ReceivePaymentReference> references = receivePayment.getReferences();
        if (references != null) {
            references.forEach(reference -> {
                reference.setReceivePayment(receivePayment);
            });
        }
        receivePaymentRepository.save(receivePayment);
        notifyUser(receivePayment);
    }


    @Override
    public ReceivePayment loadRegisterRecord(String id) {
       return receivePaymentRepository.findWithReferenceById(id).orElseThrow(() -> new BusinessException("未查询到回款记录(id" + id + ")")).afterQuery();
    }

    @Override
    public List<ReceivePayment> findByQuery(ReceivePaymentRequestForm requestForm) {
        final List<ReceivePayment> list = receivePaymentRepository.findByQuery(requestForm.getQuery());
        WithQueryUtil.build(list);
        return list;
    }

    @Override
    public Page<ReceivePayment> findByQueryPageable(ReceivePaymentRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.getPage(), requestForm.getSize());
        final Page<ReceivePayment> page = receivePaymentRepository
                .findByQuery(requestForm.getQuery(), requestForm.getNotifyUserid(), requestForm.getReceiveDateStart(), requestForm.getReceiveDateEnd(), pageable);
        WithQueryUtil.build(page);
        return page;
    }

    /**
     * TODO:
     * 统计：根据客户，项目统计
     * 发票 | 应收 | 已回款 | 余额 | 客户
     */
    @Override
    public Page<ReceivePayment> receivePaymentStats(ReceivePaymentRequestForm requestForm) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Double> criteriaQuery = criteriaBuilder.createQuery(Double.class);
        // 创建根对象
        Root<ReceivePayment> root = criteriaQuery.from(ReceivePayment.class);
        // 选择 SUM(amount)
        criteriaQuery.select(criteriaBuilder.sum(root.get("amount")));


        return null;
    }


    private List<InvoiceApplyStat> createPayingSelectQuery(ReceivePaymentRequestForm requestForm) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> selectQuery = criteriaBuilder.createQuery(Tuple.class);
        // 创建根对象
        final Root<InvoiceApply> root = selectQuery.from(InvoiceApply.class);

        final Expression<Double> sumAmount = criteriaBuilder.sum(root.get("invoiceAmount"));
        final Expression<Long> count = criteriaBuilder.count(root);
        List<Expression<?>> groupBy = dynamicGroupBy(requestForm.getStatsType(), root);
        List<Selection<?>> selections = dynamicSelections(requestForm.getStatsType(), root);
        selections.add(count.alias("invoiceCount"));
        selections.add(sumAmount.alias("paying"));
        selectQuery.multiselect(selections);
        //where
        final List<Predicate> predicates = createPayingStatsWhereClause(criteriaBuilder, root, requestForm);
        final Predicate whereAnd = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        if (!predicates.isEmpty()) {
            selectQuery.where(whereAnd);
        }
        //group
        if (groupBy != null) {
            selectQuery.groupBy(groupBy);
        }
        List<Order> orderBy = createPayingOrderBy( criteriaBuilder, root, requestForm);
        if (orderBy != null) {
            selectQuery.orderBy(orderBy);
        }
        TypedQuery<Tuple> query = entityManager.createQuery(selectQuery);
        query.setFirstResult(requestForm.getFirstResult());
        query.setMaxResults(requestForm.getSize());
        final List<Tuple> resultList = query.getResultList();
        List<InvoiceApplyStat> list = new ArrayList<>();
        for (Tuple tuple : resultList) {
            list.add(createInvoiceApplyStat(tuple, requestForm.getStatsType()));
        }
        return list;
    }

    /**
     * 应收统计
     */
    @Override
    public Page<InvoiceApplyStat> payingStats(ReceivePaymentRequestForm requestForm) {
        final List<InvoiceApplyStat> list = createPayingSelectQuery(requestForm);
        Long total = createPayingTotalCountQuery(requestForm);
        return new PageImpl<>(list, PageRequest.of(requestForm.jpaPage(), requestForm.getSize()), total);
    }

    /**
     * 查询应收总数
     */
    private Long createPayingTotalCountQuery(ReceivePaymentRequestForm requestForm) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        final Root<InvoiceApply> root = countQuery.from(InvoiceApply.class);
        //查询总数
        countQuery.select(criteriaBuilder.count(root));
        //where
        final List<Predicate> predicates = createPayingStatsWhereClause(criteriaBuilder, root, requestForm);
        final Predicate whereAnd = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        if (!predicates.isEmpty()) {
            countQuery.where(whereAnd);
        }
        countQuery.where(whereAnd);
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private List<Order> createPayingOrderBy(CriteriaBuilder criteriaBuilder, Root<InvoiceApply> root, ReceivePaymentRequestForm requestForm) {
        List<String> attrs = requestForm.getStatsType();
        if (attrs == null || attrs.isEmpty()) {
            return null;
        }
        List<Order> orderBy = new ArrayList<>();
        attrs.forEach(attr -> {
            criteriaBuilder.asc(root.get(attr));
        });
        return orderBy;
    }

    private List<Predicate> createPayingStatsWhereClause(CriteriaBuilder criteriaBuilder, Root<InvoiceApply> root, ReceivePaymentRequestForm requestForm) {

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("isFinished"), true));
        predicates.add(criteriaBuilder.equal(root.get("businessState"), "已通过"));
        if (StringUtils.isNotBlank(requestForm.getProject())) {
            predicates.add(criteriaBuilder.like(root.get("project"), QueryUtil.like(requestForm.getProject())));

        }
        if (StringUtils.isNotBlank(requestForm.getCustomerName())) {
            predicates.add(criteriaBuilder.like(root.get("clientName"), QueryUtil.like(requestForm.getCustomerName())));
        }
        if (StringUtils.isNotBlank(requestForm.getContractName())) {
            predicates.add(criteriaBuilder.like(root.get("contractName"), QueryUtil.like(requestForm.getContractName())));
        }
        if (StringUtils.isNotBlank(requestForm.getTestNo())) {
            predicates.add(criteriaBuilder.like(root.get("testNo"), QueryUtil.like(requestForm.getTestNo())));
        }
        if (requestForm.getCompany() != null) {
            CriteriaBuilder.In<Object> inClause = criteriaBuilder.in(root.get("company")).value(requestForm.getCompany());
            for (String c : requestForm.getCompany()) {
                inClause.value(c);
            }
        }
        return predicates;
    }


    private InvoiceApplyStat createInvoiceApplyStat(Tuple tuple, List<String> attrs) {
        InvoiceApplyStat stat = new InvoiceApplyStat();
        if (attrs == null) {
            return stat;
        }
        Class<?> targetClass = stat.getClass();
        tuple.getElements().forEach(element -> {
            String name = element.getAlias();
            try {
                Field field = targetClass.getDeclaredField(name);
                field.setAccessible(true);
                field.set(stat, tuple.get(name));
            } catch (NoSuchFieldException e) {
                throw new BusinessException("属性:" + name + " 不存在");
            } catch (Exception e) {
                throw new BusinessException(e);
            }
        });

        return stat;
    }


    /**
     * 动态生成查询参数
     * @param attrs 属性
     * @param root root
     */
    private List<Selection<?>> dynamicSelections(List<String> attrs, Root<InvoiceApply> root) {
        List<Selection<?>> construct = new ArrayList<>();
        if (attrs == null) {
            return construct;
        }

        attrs.forEach(attr -> {
            construct.add(root.get(attr).alias(attr));
        });
        return construct;
    }

    /**
     * 生成group by条件
     * @param attrs 属性列表
     */
    private List<Expression<?>> dynamicGroupBy(List<String> attrs, Root<InvoiceApply> root) {
        if (attrs == null) {
            return null;
        }
        List<Expression<?>> groupBy = new ArrayList<>();
        attrs.forEach(attr -> {
            groupBy.add(root.get(attr));
        });
        return groupBy;
    }

    @Override
    public void saveConfig(List<ReceivePaymentConfig> configs) {
        receivePaymentConfigRepository.deleteByType(REC_PAY_CONFIG_TYPE_NOTIFY);
        receivePaymentConfigRepository.saveAllAndFlush(configs);
    }

    @Override
    public List<User> findDefaultNotifyUsers() {
        final List<ReceivePaymentConfig> configs = receivePaymentConfigRepository.findByType(REC_PAY_CONFIG_TYPE_NOTIFY);
        List<User> users = new ArrayList<>();
        if (configs != null && !configs.isEmpty()) {
            configs.forEach(config -> {
                String value = config.getValue();
                if (StringUtils.isNotBlank(value)) {
                    User user = JsonUtil.toObject(value, new TypeReference<User>() {});
                    users.add(user);
                }
            });
        }
        return users;
    }

    @Override
    public ReceivePayment loadWithAll(String id) {
        final ReceivePayment receivePayment = receivePaymentRepository
                .findWithReferenceById(id)
                .orElseThrow(() -> new BusinessException("未查询到回款记录(id" + id + ")"))
                .afterQuery();
        //关联数据
        final List<ReceivePaymentReference> references = receivePayment.getReferences();
        if (references != null) {
            //1. 发票
            final List<String> invIds = references.stream()
                    .filter(i -> ReceivePaymentReference.TYPE_INVOICE.equals(i.getType()))
                    .map(ReceivePaymentReference::getRefId)
                    .toList();
            final List<InvoiceApply> invList = invoiceApplyRepository.findByIdIsIn(invIds);
            receivePayment.setInvoices(invList);

            //合同
            final List<String> conIds = references.stream()
                    .filter(i -> ReceivePaymentReference.TYPE_CONTRACT.equals(i.getType()))
                    .map(ReceivePaymentReference::getRefId)
                    .toList();
            final List<SaleAgreement> saList = saleAgreementRepository.findByIdIsIn(conIds);
            receivePayment.setSaleAgreements(saList);
        }
        return receivePayment;
    }

    /**
     * 通知用户
     */
    public void notifyUser(ReceivePayment receivePayment) {
        List<User> users = receivePayment.getNotifyUsers();
        if (users == null) {
            return;
        }
        if (receivePayment.isNotify()) {
            users.forEach(u -> doNotify(u, receivePayment));
        }
    }

    private void doNotify(User user, ReceivePayment receivePayment) {
        final List<PushRegister> regList = pushService.findByUserid(user.getId());
        if (regList == null) {
            return;
        }
        regList.forEach(r -> {
            //后续关联跳转到指定页面，可能需要receivePayment
            pushService.pushAndroid(user.getId(), "回款到账通知", "回款到账通知，点击查看详情", 1);
        });
    }

    @Override
    public Page<ReceivePayment> findByNotifyUsers(ReceivePaymentRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getSize());
        final Page<ReceivePayment> page = receivePaymentRepository
                .findByQuery(requestForm.getQuery(), requestForm.getNotifyUserid(), requestForm.getReceiveDateStart(), requestForm.getReceiveDateEnd(), pageable);
        page.getContent().forEach(ReceivePayment::simple);
        return page;
    }

    @Override
    public void deleteRegister(String id) {
        receivePaymentRepository.findById(id).ifPresent(receivePayment -> {
            receivePaymentRepository.deleteById(id);
        });
    }
}
