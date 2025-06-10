package com.abt.safety.listener;

import com.abt.common.model.User;
import com.abt.config.AsyncConfig;
import com.abt.safety.entity.SafetyRecord;
import com.abt.safety.event.SafetyRecordFinishedEvent;
import com.abt.safety.model.RecordStatus;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserRole;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.SystemMessage;
import com.abt.sys.service.PermissionService;
import com.abt.sys.service.SystemMessageService;
import com.abt.sys.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.abt.safety.Constants.*;

/**
 * 安全检查步骤完成监听事件
 * 对应SafetyRecordFinishedEvent
 */
@Component
@Slf4j
public class SafetyRecordFinishedListener {
    private final SystemMessageService systemMessageService;
    private final UserService<UserView, User> userService;


    public SafetyRecordFinishedListener(SystemMessageService systemMessageService, @Qualifier("sqlServerUserService") UserService<UserView, User> userService) {
        this.systemMessageService = systemMessageService;
        this.userService = userService;
    }

    /**
     * 监听安全检查步骤成功完成后发送消息
     * 如果有多个
     * @param event 事件
     */
    @Async
    @EventListener
    @Order(1)
    public void safetyRecordFinishedListener(SafetyRecordFinishedEvent event) {
        final SafetyRecord safetyRecord = event.getSafetyRecord();
        final RecordStatus state = safetyRecord.getState();
        final String dateStr = safetyRecord.getCheckTime().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
        switch (state) {
            case SUBMITTED -> sendSubmittedMsg(safetyRecord, dateStr);
            case DISPATCHED -> sendDispatchedMsg(safetyRecord, dateStr);
            case RECTIFIED -> sendRectifyMsg(safetyRecord);
            default -> log.error("未知的状态: {}", state);
        }
    }

    /**
     * 检查完成有问题发送信息给调度
     * @param record 检查记录
     * @param dateStr 检查日期
     */
    public void sendSubmittedMsg(SafetyRecord record, String dateStr) {
        String msg = "";
        if (record.isHasProblem()) {
            msg = String.format("%s已完成%s安全检查，存在问题，请查看并调度至相关负责人", record.getCreateUsername(), dateStr);

        } else {
            msg = String.format("%s已完成%s安全检查，无问题", record.getCreateUsername(), dateStr);
        }
        //可能允许有多个调度人
        final List<UserRole> ur = userService.getUserByRoleId(ROLE_DISPATCHER);
        if (ur != null && !ur.isEmpty()) {
            for (UserRole u : ur) {
                try {
                    SystemMessage systemMessage = systemMessageService.createImportantSystemMsg(u.getId(), u.getUsername() , msg, SERVICE_SAFETY, record.getId());
                    systemMessageService.sendMessage(systemMessage);
                } catch (Exception e) {
                    log.error("安全检查(SUBMITTED)-发送信息给调度人({})失败: recordId: ", u.getUsername(), e);
                }
            }
        }
    }

    /**
     * 调度完成发送信息给负责人
     * @param record 安全检查记录
     * @param dateStr 检查日期
     */
    public void sendDispatchedMsg(SafetyRecord record, String dateStr) {
        try {
            String msg = String.format("%s(%s)安全检查存在问题，请整改", record.getLocation(), dateStr);
            SystemMessage systemMessage = systemMessageService.createImportantSystemMsg(record.getRectifierId(), record.getRectifierName(), msg, SERVICE_SAFETY, record.getId());
            systemMessageService.sendMessage(systemMessage);
        } catch (Exception e) {
            log.error("安全检查(DISPATCHED)-发送信息给负责人({})失败: recordId: {}", record.getRectifierName(), record.getId(), e);
        }

    }

    /**
     * 整改完成发送给检查人和调度人
     * @param record 检查记录
     */
    public void sendRectifyMsg(SafetyRecord record) {
        String msg = String.format("%s已由%s整改完成(%s)", record.getLocation(), record.getRectifierName(), record.getRectifyTime().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
        try {
            SystemMessage sm1 = systemMessageService.createImportantSystemMsg(record.getCheckerId(), record.getCheckerName(), msg, SERVICE_SAFETY, record.getId());
            systemMessageService.sendMessage(sm1);
        } catch (Exception e) {
            log.error("安全检查(RECTIFIED)-发送信息给检查人({})失败: recordId: {}", record.getCheckerName(), record.getId(), e);
        }
        try {
            SystemMessage sm2 = systemMessageService.createImportantSystemMsg(record.getDispatcherId(), record.getDispatcherName(), msg, SERVICE_SAFETY, record.getId());
            systemMessageService.sendMessage(sm2);
        } catch (Exception e) {
            log.error("安全检查(RECTIFIED)-发送信息给调度人({})失败: recordId: {}", record.getDispatcherName(), record.getId(), e);
        }
    }
}
