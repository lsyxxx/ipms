package com.abt.market.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 结算单详细列表项。
 */
@Getter
@Setter
@NoArgsConstructor
public class SettlementDetailDTO {
    private String id;
    private BigDecimal totalAmount;
    private String clientName;
    private List<String> entrustIds = new ArrayList<>();

    public SettlementDetailDTO(String id, BigDecimal totalAmount, String clientName) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.clientName = clientName;
    }
}
