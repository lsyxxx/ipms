package com.abt.sys.controller;

import com.abt.common.model.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/test/session")
@Tag(name = "SessionController", description = "")
public class SessionController {

    /**
     * 创建session
     */
    @GetMapping("/create")
    public R<String> applyHttpSessionId(HttpServletRequest request) {
        //每次进入若没有session则创建
        final HttpSession session = request.getSession(true);
        final UUID uuid = UUID.randomUUID();
        String sid = session.getId();
        final int sessionTimeout = session.getServletContext().getSessionTimeout();
        System.out.println("session time out: " + sessionTimeout);
        R<String> r = R.success(uuid.toString(), "申请sid成功");
        r.setSid(sid);
        return r;
    }
}
