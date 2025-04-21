package com.abt.sys.service.impl;

import com.abt.common.util.TimeUtil;
import com.abt.sys.config.SystemConstants;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.SystemMessageRequestForm;
import com.abt.sys.model.entity.SystemMessage;
import com.abt.sys.repository.SystemMessageRepository;
import com.abt.sys.service.SystemMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.abt.sys.config.SystemConstants.SYSMSG_TYPE_ID_COPY;
import static com.abt.sys.config.SystemConstants.SYSMSG_TYPE_NAME_TIP;

/**
 *
 */
@Service
@Slf4j
public class SystemMessageServiceImpl implements SystemMessageService {

    private final SystemMessageRepository systemMessageRepository;

    public SystemMessageServiceImpl(SystemMessageRepository systemMessageRepository) {
        this.systemMessageRepository = systemMessageRepository;
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
        Pageable page = requestForm.createDefaultPageableWithoutSorting();
        return systemMessageRepository.findAllBy(requestForm.getToId(),
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
    public void readAll(String toId) {
        systemMessageRepository.updateReadAllByToId(toId, LocalDateTime.now());
    }

    @Override
    public void sendAll(List<SystemMessage> list) {
        systemMessageRepository.saveAll(list);
    }
}
