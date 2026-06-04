package com.abt.market.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目结算差异详情。
 */
@Getter
@Setter
@NoArgsConstructor
public class EntrustSettlementDiffDTO extends EntrustSettlementStatDTO {
    private List<EntrustSettlementDiffItemDTO> items = new ArrayList<>();
}
