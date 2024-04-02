package com.abt.finance.service.impl;

import com.abt.finance.entity.BankAccount;
import com.abt.finance.entity.CreditBook;
import com.abt.finance.entity.DebitBook;
import com.abt.finance.model.CashRequestForm;
import com.abt.finance.repository.BankAccountRepository;
import com.abt.finance.repository.CreditBookRepository;
import com.abt.finance.repository.DebitBookRepository;
import com.abt.finance.service.FinanceBookKeepingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.abt.common.util.QueryUtil.like;

/**
 * 资金流入流出
 */
@Service
public class FinanceBookKeepingServiceImpl implements FinanceBookKeepingService {

    private final CreditBookRepository creditBookRepository;
    private final DebitBookRepository debitBookRepository;

    private final BankAccountRepository bankAccountRepository;



    public FinanceBookKeepingServiceImpl(CreditBookRepository creditBookRepository, DebitBookRepository debitBookRepository, BankAccountRepository bankAccountRepository) {
        this.creditBookRepository = creditBookRepository;
        this.debitBookRepository = debitBookRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public CreditBook creditBookKeeping(CreditBook credit) {
        creditBookRepository.save(credit);
        return credit;
    }

    @Override
    public DebitBook debitBookKeeping(DebitBook debit) {
        return debitBookRepository.save(debit);
    }

    @Override
    public List<DebitBook> loadDebits(String businessId) {
        return debitBookRepository.findByBusinessIdOrderByCreateDateDesc(businessId);
    }

    @Override
    public void deleteDebitById(String id) {
        debitBookRepository.deleteById(id);
    }

    @Override
    public List<BankAccount> loadAllBankAccounts() {
        return bankAccountRepository.findAll();
    }

    @Override
    public List<CreditBook> loadCreditBookByBusinessId(String businessId) {
        return creditBookRepository.findByBusinessIdOrderByCreateDateDesc(businessId);
    }

    @Override
    public void deleteCreditById(String id) {
        creditBookRepository.deleteById(id);
    }


    @Override
    public List<CreditBook> loadCreditByCriteria(CashRequestForm criteria) {
        //criteria: businessId 实体id, expenseType费用类型, invoiceType票据类型, payType付款方式, 付款时间range，付款开户行account, 收款人
        Specification<CreditBook> spec = Specification.where(CreditSpecifications.businessIdLike(criteria))
                .and(CreditSpecifications.payDateBetween(criteria))
                .and(CreditSpecifications.receiveUserEq(criteria))
                .and(CreditSpecifications.payAccountLike(criteria));
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getLimit(),
                Sort.by(Sort.Direction.DESC, "businessId", "expenseType", "invoiceType", "payType", "payDate"));
        final Page<CreditBook> all = creditBookRepository.findAll(spec, pageable);
        return all.getContent();
    }

    static class CreditSpecifications {
        public static Specification<CreditBook> businessIdLike(CashRequestForm form) {
            return (root, query, builder) -> builder.like(root.get("businessId"), like(form.getBusinessId()));
        }

        public static Specification<CreditBook> payDateBetween(CashRequestForm form) {
            return (root, query, builder) -> builder.between(root.get("payDate"), form.getStartDate(), form.getEndDate());
        }

        public static Specification<CreditBook> payAccountLike(CashRequestForm form) {
            return (root, query, builder) -> builder.like(root.get("payAccount"), like(form.getPayAccount()));
        }

        public static Specification<CreditBook> receiveUserEq(CashRequestForm form) {
            return (root, query, builder) -> builder.equal(root.get("receiveUserid"), form.getUserid());
        }

    }

}
