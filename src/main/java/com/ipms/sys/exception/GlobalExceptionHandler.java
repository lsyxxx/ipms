package com.ipms.sys.exception;

import com.ipms.common.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 处理认证异常
     *
     * @param e
     * @return R
     */
    @ExceptionHandler(AuthenticationException.class)
    public R<Exception> handleAuthenticationException(AuthenticationException e) {
        log.error("Authentication failed! - {}", e.getMessage());
        return R.authenticationFail(e, e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public R<Exception> handleUsernameNotFoundException(AuthenticationException e) {
        log.error("handleUsernameNotFoundException -> Username not found! - {}", e.getMessage());
        return R.authenticationFail(e, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public R<Exception> handleException(AuthenticationException e) {
        log.error("Uncaught exception! - {}", e.getMessage());
        return R.fail(e.getMessage());
    }
}
