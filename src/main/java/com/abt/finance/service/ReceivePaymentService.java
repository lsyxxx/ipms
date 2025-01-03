package com.abt.finance.service;

import com.abt.finance.entity.ReceivePayment;
import com.abt.finance.model.ReceivePaymentRequestForm;

import java.util.List;

public interface ReceivePaymentService {
    /**
     * 回款登记
     * @param receivePayment 提交表单
     */
    void registerPayment(ReceivePayment receivePayment);

    /**
     * 读取数据一条回款登记
     * @param id id
     */
    ReceivePayment loadRegisterRecord(String id);

    /**
     * 查询登记记录
     * @param requestForm 搜索条件
     * @return 记录列表
     */
    List<ReceivePayment> findByQuery(ReceivePaymentRequestForm requestForm);
}
