package com.abt.wf.service.impl;

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
        if (Constants.NODE_ACC.equals(role)) {
            //会计
            entity.setAccountItemId(form.getAccountItemId());
            entity.setTaxItemId(form.getTaxItemId());
            entity.setPayType(form.getPayType());
            entity.setPayAccountId(form.getPayAccountId());
        } else if (Constants.NODE_FI_MGR.equals(role)) {
            //财务总监
            entity.setPayLevel(form.getPayLevel());
        }
    }


}
