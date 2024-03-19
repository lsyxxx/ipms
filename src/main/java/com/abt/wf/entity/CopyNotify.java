package com.abt.wf.entity;

import com.abt.sys.model.entity.NotifyMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 流程抄送
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CopyNotify extends NotifyMessage {

    private String processInstanceId;
}
