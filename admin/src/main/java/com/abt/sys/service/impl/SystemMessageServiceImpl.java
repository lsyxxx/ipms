package com.abt.sys.service.impl;

import com.abt.common.util.TimeUtil;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.repository.EmployeeRepository;
import com.abt.sys.repository.TUserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import com.abt.sys.config.SystemConstants;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.SystemMessageRequestForm;
import com.abt.sys.model.entity.SystemMessage;
import com.abt.sys.repository.SystemMessageRepository;
import com.abt.sys.service.SystemMessageService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.abt.sys.config.SystemConstants.*;

/**
 *
 */
@Service
@Slf4j
public class SystemMessageServiceImpl implements SystemMessageService {

    private final SystemMessageRepository systemMessageRepository;
    private final EmployeeRepository employeeRepository;
    private final TUserRepository tUserRepository;

    public SystemMessageServiceImpl(SystemMessageRepository systemMessageRepository,
                                    EmployeeRepository employeeRepository, TUserRepository tUserRepository) {
        this.systemMessageRepository = systemMessageRepository;
        this.employeeRepository = employeeRepository;
        this.tUserRepository = tUserRepository;
    }

    /** 批量发送在同一事务中完成，接收人按用户 ID 去重。 */
    @Override
    @Transactional
    public int sendApprovalReminder(String content) {
        UserView sender = TokenUtil.getUserFromAuthToken();
        if (tUserRepository.countApprovalReminderPermission(sender.getId()) == 0) {
            throw new AccessDeniedException("无权发送审批提醒");
        }
        if (StringUtils.isBlank(content)) {
            throw new BusinessException("请输入提醒内容");
        }
        if (content.length() > 200) {
            throw new BusinessException("提醒内容不能超过200字");
        }
        Set<String> userIds = new HashSet<>();
        List<SystemMessage> messages = new ArrayList<>();
        for (EmployeeInfo employee : employeeRepository.findApprovalReminderRecipients()) {
            if (StringUtils.isBlank(employee.getUserid()) || !userIds.add(employee.getUserid())) {
                continue;
            }
            SystemMessage message = createImportantSystemMsg(employee.getUserid(), employee.getName(),
                    content.trim(), "approvalReminder", null);
            message.setTitle("审批提醒");
            message.setFromId(sender.getId());
            message.setFromName(sender.getName());
            message.setHref("/flowinstances/wait");
            message.setImportantPriority();
            messages.add(message);
        }
        sendAll(messages);
        log.info("审批提醒发送完成，发送人={}，接收人数={}", sender.getId(), messages.size());
        return messages.size();
    }


    @Override
    public void sendMessage(SystemMessage message) {
        systemMessageRepository.save(message);
    }

    /**
     * 创建一条抄送信息
     */
    @Override
    public SystemMessage createDefaultCopyMessage(String toId, String toName, String href, String content, String service) {
        SystemMessage copyMessage = new SystemMessage();
        copyMessage.setTypeId(SystemConstants.SYSMSG_TYPE_NAME_COPY);
        copyMessage.setTypeName(SYSMSG_TYPE_ID_COPY);
        copyMessage.setFromId(SystemConstants.SYSTEM_USER_ID);
        copyMessage.setFromName(SystemConstants.SYSTEM_USER_NAME);
        copyMessage.setToId(toId);
        copyMessage.setToName(toName);
        copyMessage.setHref(href);
        copyMessage.setContent(content);
        copyMessage.setService(service);
        return copyMessage;
    }

    @Override
    public Page<SystemMessage> findUserSystemMessagesAllPageable(SystemMessageRequestForm requestForm) {
        Pageable page = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("priority"), Sort.Order.desc("createTime")));
        return systemMessageRepository.findAllBy(requestForm.getToId(),
                requestForm.buildTypeIds(),
                requestForm.getToStatus(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()),
                TimeUtil.toLocalDateTime(requestForm.getEndDate()),
                page
        );
    }

    @Override
    public Page<SystemMessage> findUserCommonMessages(SystemMessageRequestForm requestForm) {
        Pageable page = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createTime")));
        return systemMessageRepository.findCommonMessage(requestForm.getToId(),
                requestForm.buildTypeIds(),
                requestForm.getToStatus(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()),
                TimeUtil.toLocalDateTime(requestForm.getEndDate()),
                page
        );
    }


    @Override
    public Page<SystemMessage> findUserTestTaskMessages(SystemMessageRequestForm requestForm) {
        Pageable page = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("priority"), Sort.Order.desc("createTime")));
        return systemMessageRepository.findTestTaskMessage(requestForm.getToId(),
                requestForm.buildTypeIds(),
                requestForm.getToStatus(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()),
                TimeUtil.toLocalDateTime(requestForm.getEndDate()),
                page
        );
    }

    @Override
    public void readOne(String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException("未传入消息id(" + id + " )");
        }
        systemMessageRepository.updateReadById(LocalDateTime.now(), id);
    }

    @Override
    public SystemMessage createSystemMessage(String toId, String toName, String href, String content, String service) {
        SystemMessage msg = new SystemMessage();
        msg.setTypeId(SystemConstants.SYSMSG_TYPE_ID_TIP);
        msg.setTypeName(SYSMSG_TYPE_NAME_TIP);
        msg.setFromId(SystemConstants.SYSTEM_USER_ID);
        msg.setFromName(SystemConstants.SYSTEM_USER_NAME);
        msg.setToId(toId);
        msg.setToName(toName);
        msg.setHref(href);
        msg.setContent(content);
        msg.setService(service);
        return msg;
    }

    @Override
    public SystemMessage createImportantSystemMsg(@NotNull String toId, String toName, @NotNull String content, @NotNull String service, String entityId) {
        SystemMessage  msg = new SystemMessage();
        msg.setTypeId(SystemConstants.SYSMSG_TYPE_IMPORTANT);
        msg.setTypeName(SYSMSG_TYPE_NAME_IMPORTANT);
        msg.setFromId(SystemConstants.SYSTEM_USER_ID);
        msg.setFromName(SystemConstants.SYSTEM_USER_NAME);
        msg.setToId(toId);
        msg.setToName(toName);
        msg.setContent(content);
        msg.setService(service);
        msg.setEntityId(entityId);
        msg.setMsgResult(SYSMSG_RESULT_IMPORTANT);
        return msg;
    }

    @Override
    public void readAll(String toId) {
        systemMessageRepository.updateReadAllByToId(toId, LocalDateTime.now());
    }

    @Override
    public void sendAll(List<SystemMessage> list) {
        systemMessageRepository.saveAll(list);
    }

}
