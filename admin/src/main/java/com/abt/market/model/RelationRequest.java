package com.abt.market.model;

import com.abt.market.entity.SettlementRelation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 结算单保存合同
 */
@Getter
@Setter
public class RelationRequest {

    private String mid;
    private List<SettlementRelation> relationList;
    /**
     * 关联类型，可以为Null
     * 如果为Null，表示relationList中有多种类型
     * 如果不为null，那么只有单一类型
     */
    private SettlementRelationType bizType;
}
