package com.abt.finance.service.impl;

import com.abt.finance.entity.ReceivePayment;
import com.abt.finance.model.ReceivePaymentRequestForm;
import com.abt.finance.repository.ReceivePaymentRepository;
import com.abt.finance.service.ReceivePaymentService;
import com.abt.sys.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class ReceivePaymentServiceImpl implements ReceivePaymentService {
    private final ReceivePaymentRepository receivePaymentRepository;

    public ReceivePaymentServiceImpl(ReceivePaymentRepository receivePaymentRepository) {
        this.receivePaymentRepository = receivePaymentRepository;
    }


    @Override
    public void registerPayment(ReceivePayment receivePayment) {
        //关联的开票申请(receivePaymentReference)在前端组合好
        receivePaymentRepository.save(receivePayment);
    }


    @Override
    public ReceivePayment loadRegisterRecord(String id) {
       return receivePaymentRepository.findWithReferenceById(id).orElseThrow(() -> new BusinessException("未查询到回款记录(id" + id + ")"));
    }

    @Override
    public List<ReceivePayment> findByQuery(ReceivePaymentRequestForm requestForm) {
        return receivePaymentRepository.findByQuery(requestForm.getQuery());
    }

    /**
     * 统计：TODO
     * 根据发票group 及计算金额
     * 发票 | 应收 | 已回款 | 余额 | 客户
     */
    public void findListGroupByInvoice() {
        final List<ReceivePayment> all = receivePaymentRepository.findAll();
    }
}
