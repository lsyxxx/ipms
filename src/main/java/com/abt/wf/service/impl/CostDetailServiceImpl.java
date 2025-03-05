package com.abt.wf.service.impl;

import com.abt.sys.exception.BusinessException;
import com.abt.wf.entity.CostDetail;
import com.abt.wf.model.CostDetailRequestForm;
import com.abt.wf.repository.CostDetailRepository;
import com.abt.wf.service.CostDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
@Slf4j
public class CostDetailServiceImpl implements CostDetailService {

    private final CostDetailRepository costDetailRepository;

    public CostDetailServiceImpl(CostDetailRepository costDetailRepository) {
        this.costDetailRepository = costDetailRepository;
    }

    @Override
    public void save(List<CostDetail> list) {
        costDetailRepository.saveAllAndFlush(list);
    }

    @Override
    public void save(List<CostDetail> list, String refCode) {
        if (StringUtils.isBlank(refCode)) {
            throw new BusinessException("关联单据编号(refCode)不能为空!");
        }
        list.forEach(i -> i.setRefCode(refCode));
        costDetailRepository.saveAllAndFlush(list);
    }

    @Override
    public List<CostDetail> findBy(CostDetailRequestForm requestForm) {
        return costDetailRepository.findByRefCode(requestForm.getRefCode());
    }

    @Override
    public void check(List<CostDetail> list, double totalCost) {
        final Double computed = list.stream().map(CostDetail::getCost).reduce(0.00, (acc, cur) -> {
            if (cur == null) {
                cur = 0.00;
            }
            return acc + cur;
        });
        if (Double.compare(computed, totalCost) != 0) {
            throw new BusinessException("费用分摊错误! 总金额与分摊合计金额不一致!");
        }
    }

    @Override
    public void checkAndSave(List<CostDetail> list, double totalCost) {
        check(list, totalCost);
        save(list);
    }
}
