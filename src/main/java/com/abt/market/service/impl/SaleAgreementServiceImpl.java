package com.abt.market.service.impl;

import com.abt.common.config.CommonSpecification;
import com.abt.market.entity.SaleAgreement;
import com.abt.market.model.SaleAgreementRequestForm;
import com.abt.market.repository.SaleAgreementRepository;
import com.abt.market.service.SaleAgreementService;
import com.abt.sys.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static com.abt.common.util.QueryUtil.ensureProperty;

/**
 *
 */
@Service
public class SaleAgreementServiceImpl implements SaleAgreementService {

    private final SaleAgreementRepository saleAgreementRepository;


    public SaleAgreementServiceImpl(SaleAgreementRepository saleAgreementRepository) {
        this.saleAgreementRepository = saleAgreementRepository;
    }

    @Override
    public Page<SaleAgreement> findPaged(SaleAgreementRequestForm requestForm) {
        Pageable page = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Direction.ASC, "sortNo"));
        SaleAgreementSpecification spec = new SaleAgreementSpecification();
        Specification<SaleAgreement> cr = Specification.where(spec.typeEqual(requestForm, "type"))
                .and(spec.nameLike(requestForm, "name"))
                .and(spec.attributeEqual(requestForm))
                .and(spec.partyAEqual(requestForm))
                .and(spec.partyBEqual(requestForm))
                ;
        return saleAgreementRepository.findAll(cr, page);
    }

    @Override
    public void delete(String id) {
        ensureProperty(id, "合同id");
        saleAgreementRepository.deleteById(id);
    }

    @Override
    public List<String> findAllPartyA() {
        return saleAgreementRepository.findAll().stream().map(SaleAgreement::getPartyA).distinct().toList();
    }

    @Override
    public List<SaleAgreement> findSaleAgreementCreatedByCurrentWeek() {
        //本周登记
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return saleAgreementRepository.findByCreateDateBetweenOrderByCreateDateDesc(startOfWeek.atStartOfDay(), endOfWeek.atStartOfDay());
    }

    @Override
    public List<SaleAgreement> findSaleAgreementCreatedByCurrentMonth() {
        // 获取当前日期
        LocalDate today = LocalDate.now();
        // 获取当前年份和月份
        YearMonth currentMonth = YearMonth.of(today.getYear(), today.getMonth());
        // 获取当前月份的第一天:yyyy-mm-dd
        LocalDate startOfMonth = currentMonth.atDay(1);
        // 获取当前月份的最后一天:yyyy-mm-dd
        LocalDate endOfMonth = currentMonth.atEndOfMonth();
        return saleAgreementRepository.findByCreateDateBetweenOrderByCreateDateDesc(startOfMonth.atStartOfDay(), endOfMonth.atStartOfDay());
    }

    @Override
    public long countAllSaleAgreement() {
        return saleAgreementRepository.count();
    }

    @Override
    public void saveSaleAgreement(SaleAgreement saleAgreement) {
        long count = saleAgreementRepository.count();
        count = count + 1;
        saleAgreement.setSortNo(count);
        saleAgreementRepository.save(saleAgreement);
    }

    @Override
    public SaleAgreement LoadSaleAgreement(String id) {
        final SaleAgreement saleAgreement = saleAgreementRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到销售合同(id=" + id + ")"));
        saleAgreement.format();
        return saleAgreement;
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
    }
}
