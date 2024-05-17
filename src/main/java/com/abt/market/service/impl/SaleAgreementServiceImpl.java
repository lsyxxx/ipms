package com.abt.market.service.impl;

import com.abt.common.config.CommonSpecification;
import com.abt.market.entity.SaleAgreement;
import com.abt.market.model.SaleAgreementRequestForm;
import com.abt.market.repository.SaleAgreementRepository;
import com.abt.market.service.SaleAgreementService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
                .and(spec.attributeEqual(requestForm))
                .and(spec.partyAEqual(requestForm));
        return saleAgreementRepository.findAll(cr, page);
    }

    @Override
    public void delete(String id) {
        ensureProperty(id, "合同id");
        saleAgreementRepository.deleteById(id);
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
