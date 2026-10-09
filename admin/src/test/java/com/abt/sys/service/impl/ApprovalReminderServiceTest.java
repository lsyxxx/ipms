package com.abt.sys.service.impl;

import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.model.entity.SystemMessage;
import com.abt.sys.repository.EmployeeRepository;
import com.abt.sys.repository.SystemMessageRepository;
import com.abt.sys.repository.TUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** 不连接数据库、不实际发送消息的审批提醒服务测试。 */
class ApprovalReminderServiceTest {
    private final SystemMessageRepository messages = mock(SystemMessageRepository.class);
    private final EmployeeRepository employees = mock(EmployeeRepository.class);
    private final TUserRepository users = mock(TUserRepository.class);
    private final SystemMessageServiceImpl service = new SystemMessageServiceImpl(messages, employees, users);

    @BeforeEach
    void authenticate() {
        UserView sender = new UserView().setId("sender").setName("发送人");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(sender, "", List.of()));
        when(users.countApprovalReminderPermission("sender")).thenReturn(1L);
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void sendsOneImportantUnreadMessagePerUser() {
        when(employees.findApprovalReminderRecipients()).thenReturn(List.of(
                new EmployeeInfo("u1", "e1", "员工一", "d1"),
                new EmployeeInfo("u1", "e2", "员工一", "d1"),
                new EmployeeInfo("u2", "e3", "员工二", "d2"),
                new EmployeeInfo("", "e4", "无账号", "d2")));
        assertEquals(2, service.sendApprovalReminder("  请及时处理未完成的审批。  "));
        ArgumentCaptor<List<SystemMessage>> captor = ArgumentCaptor.forClass((Class) List.class);
        verify(messages).saveAll(captor.capture());
        assertEquals(List.of("u1", "u2"), captor.getValue().stream().map(SystemMessage::getToId).toList());
        for (SystemMessage message : captor.getValue()) {
            assertEquals("important", message.getMsgResult());
            assertEquals(0, message.getToStatus());
            assertEquals(999, message.getPriority());
            assertEquals("approvalReminder", message.getService());
            assertEquals("/flowinstances/wait", message.getHref());
            assertEquals("sender", message.getFromId());
            assertEquals("请及时处理未完成的审批。", message.getContent());
        }
    }

    @Test
    void deniesSenderWithoutMenuPermission() {
        when(users.countApprovalReminderPermission("sender")).thenReturn(0L);
        assertThrows(AccessDeniedException.class, () -> service.sendApprovalReminder("提醒"));
        verifyNoInteractions(employees, messages);
    }

    @Test
    void rejectsBlankAndOverlongContent() {
        assertThrows(BusinessException.class, () -> service.sendApprovalReminder("  "));
        assertThrows(BusinessException.class, () -> service.sendApprovalReminder("字".repeat(201)));
        verifyNoInteractions(employees, messages);
    }

    @Test
    void accepts200CharactersAndHandlesNoRecipients() {
        when(employees.findApprovalReminderRecipients()).thenReturn(List.of());
        assertEquals(0, service.sendApprovalReminder("字".repeat(200)));
        verify(messages).saveAll(List.of());
    }
}
