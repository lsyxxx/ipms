package com.ipms.sys;

import com.ipms.sys.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 处理认证异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(AuthenticationException.class)
    public R<Exception> handleAuthenticationException(AuthenticationException e) {
        log.error("Authentication failed! - {}", e.getMessage());
        return R.authenticationFail(e, e.getMessage());
    }
}
