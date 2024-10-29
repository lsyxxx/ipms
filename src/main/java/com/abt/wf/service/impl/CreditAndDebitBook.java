package com.abt.wf.service.impl;

import com.abt.finance.entity.CreditBook;
import com.abt.finance.service.ICreditBook;
import com.abt.wf.config.Constants;
import org.springframework.stereotype.Service;

/**
 * 资金流入流出相关
 */
@Service
public class CreditAndDebitBook<T extends ICreditBook> {

    /**
     * 将表单中的资金流出数据复制到业务实体类中
     * @param form 表单
     * @param entity 业务实体类
     */
    public void setCreditBookProperty(T form, T entity, String role) {
        if (role == null)
            return;
        switch (role) {
            case Constants.NODE_ACC -> {
                //会计
            }
            case Constants.NODE_FI_MGR -> {
                entity.setPayLevel(form.getPayLevel());
                entity.setAccountItemId(form.getAccountItemId());
                entity.setTaxItemId(form.getTaxItemId());
            }
            case Constants.NODE_CASHIER -> {
                entity.setPayAccountId(form.getPayAccountId());
                entity.setPayType(form.getPayType());
                entity.setPayDate(form.getPayDate());
            }
        }
    }


    /**
     * 业务信息
     * @param form 业务实体
     * @param creditBook 资金流出记录
     */
    public CreditBook setBusiness(T form, CreditBook creditBook) {
        if (creditBook == null) {
            creditBook = new CreditBook();
        }
        creditBook.setFileJson(form.getFileJson());
        creditBook.setVoucherNum(form.getVoucherNum());
        return creditBook;

    }


}
