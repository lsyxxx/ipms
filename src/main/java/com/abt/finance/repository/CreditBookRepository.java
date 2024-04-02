package com.abt.finance.repository;

import com.abt.finance.entity.CreditBook;
import com.abt.finance.entity.DebitBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CreditBookRepository extends JpaRepository<CreditBook, String>, JpaSpecificationExecutor<CreditBook> {
    /**
     * 根据业务ID查询资金流出记录
     * @param businessId 业务id
     */
    List<CreditBook> findByBusinessIdOrderByCreateDateDesc(String businessId);
}
