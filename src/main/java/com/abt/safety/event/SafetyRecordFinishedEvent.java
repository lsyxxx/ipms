package com.abt.safety.event;

import com.abt.safety.entity.SafetyRecord;
import com.abt.safety.entity.SafetyRectify;
import com.abt.sys.model.entity.SystemMessage;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 安全检测步骤完成事件
 * 1. 检查完成且有问题，触发事件（通知调度人调度）
 * 2. 调度完成，触发事件（通知负责人整改）
 * 3. 整改完成（通知检查人/调度人整改完成）
 */
@Getter
public class SafetyRecordFinishedEvent extends ApplicationEvent {
    //业务对象
    private final SafetyRecord safetyRecord;

    private final SafetyRectify safetyRectify;

    public SafetyRecordFinishedEvent(Object source, SafetyRecord safetyRecord, SafetyRectify safetyRectify) {
        super(source);
        this.safetyRecord = safetyRecord;
        this.safetyRectify = safetyRectify;
    }
}
