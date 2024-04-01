package com.abt.wf.service;

import com.abt.finance.entity.CreditBook;
import com.abt.finance.entity.DebitBook;

/**
 * 记账
 */
public interface BookKeepingService<T> {

//    /**
//     * 生成记账信息
//     * @param form 业务表单
//     */
//    CreditBook createCreditBook(T form);
//
//    /**
//     * 生成贷方资金流出记录
//     * @param form 业务表单
//     */
//    DebitBook createDebitBook(T form);

    /**
     * 用户是否有记账权限
     */
    boolean bookKeepAccess(String userid);

//    /**
//     * 生成业务链接
//     * @param form 业务表单
//     */
//    String businessUrl(T form);
}
