package com.abt.flow.model;

import com.abt.common.model.BusinessLog;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 流程操作记录
 */
@Slf4j
@Data
public class FlowOperationLogVo extends BusinessLog {

    /**
     * 流程节点名称
     * 比如：部门审核
     */
    private String activityName;

    /**
     * 结果
     */
    private String result;

    /**
     * 备注
     */
    private String comment;


    @Override
    protected String simpleLog() {
        return new StringBuilder()
                .append(this.getUser()).append(" ")
                .append("[").append(this.getAction()).append("] ")
                .append(result)
                .toString();
    }
}
