package com.abt.finance.service.impl;

import com.abt.finance.entity.CreditBook;
import com.abt.finance.model.CreditBookRequestForm;
import com.abt.finance.repository.CreditBookRepository;
import com.abt.finance.service.CreditBookService;
import com.abt.finance.service.ICreditBook;
import com.abt.wf.repository.LoanRepository;
import com.abt.wf.repository.PayVoucherRepository;
import com.abt.wf.repository.ReimburseRepository;
import com.abt.wf.repository.TripMainRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

import static com.abt.wf.config.Constants.*;


/**
 * 资金流出
 */
@Service
@Slf4j
public class CreditBookServiceImpl implements CreditBookService {

    private final CreditBookRepository creditBookRepository;
    private final ReimburseRepository reimburseRepository;
    private final PayVoucherRepository payVoucherRepository;
    private final LoanRepository loanRepository;
    private final TripMainRepository tripMainRepository;

    public CreditBookServiceImpl(CreditBookRepository creditBookRepository, ReimburseRepository reimburseRepository, PayVoucherRepository payVoucherRepository, LoanRepository loanRepository, TripMainRepository tripMainRepository) {
        this.creditBookRepository = creditBookRepository;
        this.reimburseRepository = reimburseRepository;
        this.payVoucherRepository = payVoucherRepository;
        this.loanRepository = loanRepository;
        this.tripMainRepository = tripMainRepository;
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
    public void delete(String id) {
        Assert.hasText(id, "资金流出记录id不能为空");
        creditBookRepository.deleteById(id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ICreditBook> T loadBusiness(String type, String id) {
        Assert.hasText(type, "流程-业务类型(type)不能为空");
        Assert.hasText(id, "流程-审批编号(id)不能为空");
        return switch (type) {
            case SERVICE_RBS -> (T) reimburseRepository.findById(id).orElse(null);
            case SERVICE_PAY -> (T) payVoucherRepository.findById(id).orElse(null);
            case SERVICE_LOAN -> (T) loanRepository.findById(id).orElse(null);
            case SERVICE_TRIP -> (T) tripMainRepository.findById(id).orElse(null);
            default -> {
                log.warn("未知的业务类型: {}", type);
                yield null;
            }
        };
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
