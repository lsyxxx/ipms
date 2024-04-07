package com.abt.wf.service;

import com.abt.wf.entity.PayVoucher;
import com.abt.wf.model.PayVoucherRequestForm;

public interface PayVoucherService extends WorkFlowService<PayVoucher>, BusinessQueryService<PayVoucherRequestForm, PayVoucher>{
}
