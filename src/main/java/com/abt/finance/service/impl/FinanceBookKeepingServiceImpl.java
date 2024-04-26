package com.abt.finance.service.impl;

import com.abt.common.model.User;
import com.abt.finance.entity.BankAccount;
import com.abt.finance.entity.CreditBook;
import com.abt.finance.entity.DebitBook;
import com.abt.finance.model.CashRequestForm;
import com.abt.finance.repository.BankAccountRepository;
import com.abt.finance.repository.CreditBookRepository;
import com.abt.finance.repository.DebitBookRepository;
import com.abt.finance.service.FinanceBookKeepingService;
import com.abt.sys.model.dto.UserRole;
import com.abt.sys.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.abt.common.util.QueryUtil.like;
import static com.abt.finance.config.Constants.AUTH_BOOKKEEPING;
import static com.abt.finance.config.Constants.ROLE_FI_MGR;

/**
 * 资金流入流出
 */
@Service
@Slf4j
public class FinanceBookKeepingServiceImpl implements FinanceBookKeepingService {

    private final CreditBookRepository creditBookRepository;
    private final DebitBookRepository debitBookRepository;

    private final BankAccountRepository bankAccountRepository;
    private final UserService<User, User> userService;



    public FinanceBookKeepingServiceImpl(CreditBookRepository creditBookRepository, DebitBookRepository debitBookRepository,
                                         BankAccountRepository bankAccountRepository,
                                         @Qualifier("sqlServerUserService") UserService userService) {
        this.creditBookRepository = creditBookRepository;
        this.debitBookRepository = debitBookRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.userService = userService;
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
                .and(CreditSpecifications.receiveUserLike(criteria))
                .and(CreditSpecifications.payAccountEq(criteria));
        Pageable pageable = PageRequest.of(criteria.jpaPage(), criteria.getLimit(),
                Sort.by(Sort.Direction.DESC, "businessId", "payDate", "payAccount", "expenseType", "invoiceType", "payType"));
        final Page<CreditBook> all = creditBookRepository.findAll(spec, pageable);
        return all.getContent();
    }

    @Override
    public CreditBook initCreditBookApplyForm(CreditBook form) {
        form.setPayDate(LocalDateTime.now());
        //finMgr
        final List<UserRole> user = userService.getUserByRoleId(ROLE_FI_MGR);
        if (CollectionUtils.isEmpty(user)) {
            return form;
        }
        form.setFinanceManagerId(user.get(0).getId());
        form.setFinanceManagerName(user.get(0).getUsername());
        //bankAccount
        bankAccountRepository.findById(form.getPayAccountId()).ifPresent(bankAccount -> {
            form.setPayAccount(bankAccount.getAccount());
            form.setPayBank(bankAccount.getBank());
        });
        return form;
    }

    @Override
    public boolean hasCreditBookKeepingAccess(String userid) {
        //拥有记账角色, Role表中记账角色，则允许记账
        final List<UserRole> roleList = userService.getUserRoleByUserid(userid);
        log.trace("======== user {}, rolelist: {}", userid, roleList);
        return roleList.stream().anyMatch(i -> AUTH_BOOKKEEPING.equals(i.getRoleId()));
    }

    public void createCreditBookExcel() {

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

        public static Specification<CreditBook> payAccountEq(CashRequestForm form) {
            return (root, query, builder) -> builder.equal(root.get("payAccount"), form.getPayAccount());
        }

        public static Specification<CreditBook> receiveUserLike(CashRequestForm form) {
            return (root, query, builder) -> builder.equal(root.get("receiveUserid"), like(form.getUserid()));
        }

    }

}
