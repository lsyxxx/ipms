package com.abt.market.service.impl;

import com.abt.common.config.CommonSpecification;
import com.abt.common.util.FileUtil;
import com.abt.common.util.JsonUtil;
import com.abt.common.util.QueryUtil;
import com.abt.common.util.TimeUtil;
import com.abt.market.entity.SaleAgreement;
import com.abt.market.model.SaleAgreementRequestForm;
import com.abt.market.repository.SaleAgreementRepository;
import com.abt.market.service.SaleAgreementService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.CountQuery;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.abt.common.util.QueryUtil.ensureProperty;

/**
 *
 */
@Service
@Slf4j
public class SaleAgreementServiceImpl implements SaleAgreementService {

    private final SaleAgreementRepository saleAgreementRepository;


    public SaleAgreementServiceImpl(SaleAgreementRepository saleAgreementRepository) {
        this.saleAgreementRepository = saleAgreementRepository;
    }

    @Override
    public Page<SaleAgreement> findByQuery(SaleAgreementRequestForm requestForm) {
        Pageable page = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Direction.DESC, "sortNo"));
        return saleAgreementRepository.findByQuery(requestForm.getQuery(), page);
    }

    @Override
    public Page<SaleAgreement> findPaged(SaleAgreementRequestForm requestForm) {
        Pageable page = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Direction.DESC, "sortNo"));
        SaleAgreementSpecification spec = new SaleAgreementSpecification();
        Specification<SaleAgreement> cr = Specification.where(spec.typeEqual(requestForm, "type"))
                .and(spec.nameLike(requestForm, "name"))
                .and(spec.attributeEqual(requestForm))
                .and(spec.partyAEqual(requestForm))
                .and(spec.partyBEqual(requestForm))
                .and(spec.queryLike(requestForm))
                ;
        final Page<SaleAgreement> all = saleAgreementRepository.findAll(cr, page);
        all.getContent().forEach(SaleAgreement::format);
        return all;
    }

    @Override
    public void delete(String id) {
        ensureProperty(id, "合同id");
        saleAgreementRepository.findById(id).ifPresent(i -> {
            if (i.getFileList() != null) {
                try {
                    final List<Map<String, String>> list = JsonUtil.ObjectMapper().readValue(i.getFileList(), new TypeReference<List<Map<String, String>>>() {});
                    list.forEach(map -> {
                        String url = map.get("url");
                        FileUtil.deleteFile(url);
                    });
                } catch (JsonProcessingException e) {
                    log.error("JSON转换异常: ", e);
                }
            }
        });

        saleAgreementRepository.deleteById(id);
    }

    @Override
    public List<String> findAllPartyA() {
        return saleAgreementRepository.findAll().stream().map(SaleAgreement::getPartyA).distinct().toList();
    }

    @Override
    public List<SaleAgreement> findSaleAgreementCreatedByCurrentWeek() {
        //本周登记
        return saleAgreementRepository.findByCreateDateBetweenOrderByCreateDateDesc(TimeUtil.startDayOfCurrentWeek().atStartOfDay()
                , TimeUtil.endDayOfCurrentWeek() .atStartOfDay());
    }

    @Override
    public List<SaleAgreement> findSaleAgreementCreatedByCurrentMonth() {
        return saleAgreementRepository.findByCreateDateBetweenOrderByCreateDateDesc(TimeUtil.startDayOfCurrentMonth().atStartOfDay()
                , TimeUtil.endDayOfCurrentMonth().atStartOfDay());
    }

    @Override
    public List<SaleAgreement> findSaleAgreementCreatedByCurrentYear() {
        LocalDate today = LocalDate.now();
        // 获取当前年份和月份
        LocalDate startDay = LocalDate.of(today.getYear(), 1, 1);
        LocalDate endDay = LocalDate.of(today.getYear(), 12, 31);
        return saleAgreementRepository.findByCreateDateBetweenOrderByCreateDateDesc(startDay.atStartOfDay(), endDay.atStartOfDay());
    }

    @Override
    public long countAllSaleAgreement() {
        return saleAgreementRepository.count();
    }

    @Override
    public void saveSaleAgreement(SaleAgreement saleAgreement) {
        saleAgreementRepository.save(saleAgreement);
    }

    @Override
    public SaleAgreement LoadSaleAgreement(String id) {
        final SaleAgreement saleAgreement = saleAgreementRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到销售合同(id=" + id + ")"));
        saleAgreement.format();
        return saleAgreement;
    }


    @Override
    public Map<String, Object> marketBoardData(int currentYear) {
        //市场看板数据
        //返回的数据, key是名称
        Map<String, Object> boardData = new HashMap<>();
        //--- 统计数字
        //本周新增销售合同
        final int weekCount = saleAgreementRepository.countByCreateDateBetween(TimeUtil.startDayOfCurrentWeek().atStartOfDay(), TimeUtil.endDayOfCurrentWeek().atStartOfDay());
        boardData.put("saleAgreementCountWeek", weekCount);
        //本月新增
        final int monthCount = saleAgreementRepository.countByCreateDateBetween(TimeUtil.startDayOfCurrentMonth().atStartOfDay(), TimeUtil.endDayOfCurrentMonth().atStartOfDay());
        boardData.put("saleAgreementCountMonth", monthCount);
        //当年全部合同
        //合同金额，仅统计有金额的
        final List<SaleAgreement> yearContractList = findSaleAgreementCreatedByCurrentYear();
        boardData.put("saleAgreementCountYear", yearContractList.size());
        //合同金额
        final BigDecimal contractAmount = calculateContractAmount(yearContractList);
        boardData.put("contractAmount", contractAmount);
        //合同最多客户
        final CountQuery partyACountQuery = findPartyAWithMostContracts();
        boardData.put("partyACountQuery", partyACountQuery);
        //全部客户数量
        final int partyACount = saleAgreementRepository.countAllDistinctByPartyA();
        boardData.put("partyACount", partyACount);

        //-- 图表数据
        //每月合同分布图
        Map<Integer, Long> contractsByMonth = yearContractList.stream()
                .collect(Collectors.groupingBy(
                        sa -> sa.getCreateDate().getMonthValue(),
                        Collectors.counting()
                ));
        final Map<Integer, Long> saPerMonth = IntStream.rangeClosed(1, 12).boxed().collect(Collectors.toMap(month -> month, month -> contractsByMonth.getOrDefault(month, 0L)));
        boardData.put("createContractDataset", saPerMonth);
        //乙方合同分布
        final Map<String, Long> countByPartyB = yearContractList.stream().collect(Collectors.groupingBy(SaleAgreement::getPartyB, Collectors.counting()));
        boardData.put("partyBContractDataset", countByPartyB);
        return boardData;
    }

    public BigDecimal calculateContractAmount(List<SaleAgreement> list) {
        if (list == null || list.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal acc = BigDecimal.ZERO;
        for (SaleAgreement agr : list) {
            String amtStr = agr.getAmount();
            if (StringUtils.isBlank(amtStr)) {
                continue;
            }
            try {
                BigDecimal cur = new BigDecimal(amtStr);
                acc = acc.add(cur);
            } catch (NumberFormatException e) {
                log.warn("合同金额不是数字，不能计算 - {}", amtStr);
            }
        }
        return acc;
    }

    public CountQuery findPartyAWithMostContracts() {
        //使用分页限制只查询一个
        List<CountQuery> list  = saleAgreementRepository.findPartyAWithMostContracts(PageRequest.of(0, 1));
        return list.isEmpty() ?  CountQuery.empty() : list.get(0);
    }



    static class SaleAgreementSpecification extends CommonSpecification<SaleAgreementRequestForm, SaleAgreement> {
        public Specification<SaleAgreement> attributeEqual(SaleAgreementRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isBlank(form.getAttribute())) {
                    return null;
                }
                return builder.equal(root.get("attribute"), form.getAttribute());
            };
        }

        public Specification<SaleAgreement> partyAEqual(SaleAgreementRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isBlank(form.getPartyA())) {
                    return null;
                }
                return builder.equal(root.get("partyA"), form.getPartyA());
            };
        }

        public Specification<SaleAgreement> partyBEqual(SaleAgreementRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isBlank(form.getPartyB())) {
                    return null;
                }
                return builder.equal(root.get("partyB"), form.getPartyB());
            };
        }

        /**
         * 关键词模糊查询，合同名称/合同编号/甲方名称
         */
        public Specification<SaleAgreement> queryLike(SaleAgreementRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isBlank(form.getQuery())) {
                    return builder.conjunction();
                }
                Predicate codeLike = builder.like(root.get("code"), QueryUtil.like(form.getQuery()));
                Predicate nameLike = builder.like(root.get("name"), QueryUtil.like(form.getQuery()));
                Predicate partyALike = builder.like(root.get("partyA"), QueryUtil.like(form.getQuery()));
                return builder.or(codeLike, nameLike, partyALike);
            };
        }
    }
}
