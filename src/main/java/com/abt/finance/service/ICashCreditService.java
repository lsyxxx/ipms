package com.abt.finance.service;

import com.abt.finance.entity.CreditBook;

import java.util.List;

public interface ICashCreditService<T extends ICreditBook> {

    List<CreditBook> loadCreditBook();

    /**
     * 写入资金流出激流
     * @param biz 业务实体
     */
    void writeCreditBook(T biz);


    /**
     * 读取业务信息
     */
    T loadBusiness(String businessId);
}
