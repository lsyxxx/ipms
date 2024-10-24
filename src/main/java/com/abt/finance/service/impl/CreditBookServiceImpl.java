package com.abt.finance.service.impl;

import com.abt.finance.entity.CreditBook;
import com.abt.finance.model.CreditBookRequestForm;
import com.abt.finance.repository.CreditBookRepository;
import com.abt.finance.service.CreditBookService;
import com.abt.finance.service.ICashCreditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Optional;


/**
 * 资金流出
 */
@Service
@Slf4j
public class CreditBookServiceImpl implements CreditBookService {

    private final CreditBookRepository creditBookRepository;

    public CreditBookServiceImpl(CreditBookRepository creditBookRepository) {
        this.creditBookRepository = creditBookRepository;
    }


    @Override
    public void saveCreditBook(CreditBook creditBook) {
        creditBookRepository.save(creditBook);
    }

    @Override
    public Page<CreditBook> findBySpecification(CreditBookRequestForm form) {
        Specification<CreditBook> spec = Specification.where(CreditBookSpecifications.companyIn(form))
                .and(CreditBookSpecifications.serviceIn(form))
                .and(CreditBookSpecifications.payAccountIn(form));
        Pageable pageable = PageRequest.of(form.jpaPage(), form.getLimit(), Sort.by(Sort.Direction.DESC, "createDate"));
        return creditBookRepository.findAll(spec, pageable);
    }


    @Override
    public Optional<CreditBook> findById(String id) {
        return creditBookRepository.findById(id);
    }

    static class CreditBookSpecifications {
        public static Specification<CreditBook> companyIn(CreditBookRequestForm form) {
            return (root, query, builder) -> {
                if (!CollectionUtils.isEmpty(form.getCompanies())) {
                    return root.get("company").in(form.getCompanies());
                }
                return builder.conjunction();
            };
        }

        public static Specification<CreditBook> serviceIn(CreditBookRequestForm form) {
            return (root, query, builder) -> {
                if (!CollectionUtils.isEmpty(form.getServiceList())) {
                    return root.get("serviceName").in(form.getServiceList());
                }
                return builder.conjunction();
            };
        }
        public static Specification<CreditBook> payAccountIn(CreditBookRequestForm form) {
            return (root, query, builder) -> {
                if (!CollectionUtils.isEmpty(form.getPayAccountList())) {
                    return root.get("payAccountId").in(form.getPayAccountList());
                }
                return builder.conjunction();
            };
        }


    }
}
