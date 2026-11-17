package com.abt.testing.controller;

import com.abt.testing.config.Constants;
import com.abt.testing.model.ReportCoverInfoRequest;
import com.abt.testing.service.ReportCoverInfoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/** 使用真实方法权限代理，验证编辑、删除独立授权且未授权时不执行业务。 */
class ReportCoverInfoAuthorizationTest {
    private AnnotationConfigApplicationContext context;
    private ReportCoverInfoController controller;
    private ReportCoverInfoService service;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
        context = new AnnotationConfigApplicationContext(SecurityConfiguration.class);
        controller = context.getBean(ReportCoverInfoController.class);
        service = context.getBean(ReportCoverInfoService.class);
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
        context.close();
    }

    @Test
    void unauthenticatedCallsNeverReachService() {
        assertThrows(AuthenticationCredentialsNotFoundException.class,
                () -> controller.update(new ReportCoverInfoRequest()));
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> controller.delete("cover"));
        verifyNoInteractions(service);
    }

    @Test
    void unrelatedRoleCannotEditOrDelete() {
        authenticate("JS_OTHER");
        assertThrows(AccessDeniedException.class, () -> controller.update(new ReportCoverInfoRequest()));
        assertThrows(AccessDeniedException.class, () -> controller.delete("cover"));
        verifyNoInteractions(service);
    }

    @Test
    void editorCanUpdateButCannotDelete() {
        authenticate(Constants.ROLE_REPORT_COVER_EDIT);
        ReportCoverInfoRequest request = new ReportCoverInfoRequest();
        controller.update(request);
        verify(service).update(request);
        assertThrows(AccessDeniedException.class, () -> controller.delete("cover"));
        verify(service, never()).delete(anyString());
    }

    @Test
    void deletionRoleCanDeleteButCannotEdit() {
        authenticate(Constants.ROLE_REPORT_COVER_DEL);
        controller.delete("cover");
        verify(service).delete("cover");
        assertThrows(AccessDeniedException.class, () -> controller.update(new ReportCoverInfoRequest()));
        verify(service, never()).update(any());
    }

    private void authenticate(String... roles) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "user", "unused", Arrays.stream(roles).map(SimpleGrantedAuthority::new).toList()));
    }

    /** 仅加载与生产一致的 Secured 方法校验，不启动 Web 或数据库。 */
    @Configuration
    @EnableMethodSecurity(securedEnabled = true)
    static class SecurityConfiguration {
        @Bean
        ReportCoverInfoService reportCoverInfoService() {
            return mock(ReportCoverInfoService.class);
        }

        @Bean
        ReportCoverInfoController reportCoverInfoController(ReportCoverInfoService service) {
            return new ReportCoverInfoController(service);
        }
    }
}
