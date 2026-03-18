package com.abt.wf.service;

import com.abt.finance.service.ICashCreditService;
import com.abt.wf.entity.PayVoucher;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.PayVoucherRequestForm;

public interface PayVoucherService extends WorkFlowService<PayVoucher>, BusinessService<PayVoucherRequestForm, PayVoucher>, ICashCreditService<PayVoucher> {
    PayVoucher loadEntityWithCurrentTask(String id);
}
