package com.abt.finance.service;

import com.abt.finance.model.ReceivableDto;

import java.util.List;

public interface AccountCurrentService {

    /**
     * 验证参数
     * @param recType
     */
    void validate(String recType);


    /**
     * 根据收款类型获取收款列表
     * @param recType
     * @return
     */
    List<ReceivableDto> getReceivables(String recType);
}
