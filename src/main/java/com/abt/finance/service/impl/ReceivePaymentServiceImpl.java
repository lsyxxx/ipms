package com.abt.finance.service.impl;

import com.abt.common.model.User;
import com.abt.common.util.JsonUtil;
import com.abt.finance.entity.ReceivePayment;
import com.abt.finance.entity.ReceivePaymentConfig;
import com.abt.finance.entity.ReceivePaymentReference;
import com.abt.finance.model.ReceivePaymentRequestForm;
import com.abt.finance.repository.ReceivePaymentConfigRepository;
import com.abt.finance.repository.ReceivePaymentRepository;
import com.abt.finance.service.ReceivePaymentService;
import com.abt.sys.exception.BusinessException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.abt.finance.config.Constants.REC_PAY_CONFIG_TYPE_NOTIFY;

/**
 *
 */
@Service
public class ReceivePaymentServiceImpl implements ReceivePaymentService {
    private final ReceivePaymentRepository receivePaymentRepository;
    private final ReceivePaymentConfigRepository receivePaymentConfigRepository;

    public ReceivePaymentServiceImpl(ReceivePaymentRepository receivePaymentRepository, ReceivePaymentConfigRepository receivePaymentConfigRepository) {
        this.receivePaymentRepository = receivePaymentRepository;
        this.receivePaymentConfigRepository = receivePaymentConfigRepository;
    }


    @Override
    public void registerPayment(ReceivePayment receivePayment) {
        //关联的开票申请(receivePaymentReference)在前端组合好
        final List<ReceivePaymentReference> references = receivePayment.getReferences();
        if (references != null) {
            references.forEach(reference -> {
                reference.setReceivePayment(receivePayment);
            });
        }
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

    @Override
    public void saveConfig(List<ReceivePaymentConfig> configs) {
        receivePaymentConfigRepository.saveAllAndFlush(configs);
    }

    @Override
    public List<User> findDefaultNotifyUsers() {
        final List<ReceivePaymentConfig> configs = receivePaymentConfigRepository.findByType(REC_PAY_CONFIG_TYPE_NOTIFY);
        List<User> users = new ArrayList<>();
        if (configs != null && !configs.isEmpty()) {
            configs.forEach(config -> {
                String value = config.getValue();
                if (StringUtils.isNotBlank(value)) {
                    User user = JsonUtil.toObject(value, new TypeReference<User>() {});
                    users.add(user);
                }
            });
        }
        return users;
    }

}
