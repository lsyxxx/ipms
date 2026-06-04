package com.abt.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 关联业务的公共VO
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BizRelatedVO {

    /**
     * 业务实体ID
     */
    private String id;

    /**
     * 业务类型
     * 使用serviceName(DEF_KEY_)
     */
    private String bizType;

    /**
     * 业务中文名称
     */
    private String bizName;

    /**
     * 事由说明
     */
    private String reason;

    /**
     * 金额
     */
    private Double cost;

    /**
     * 描述，可以自定义
     */
    private String desc;



    /**
     * 申请人
     */
    private String applyUsername;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

}
