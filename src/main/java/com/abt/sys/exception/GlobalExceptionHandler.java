package com.abt.sys.exception;

import com.abt.common.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 处理认证异常
     * !注解@ExceptionHandler({class1})和 handleAuthenticationException({class2}): class1和class2异常类型必须一致
     *
     * @param e AuthenticationException类异常
     * @return R
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R<Exception> handleAuthenticationException(AuthenticationException e) {
        log.error("Authentication failed! - {}", e.getMessage());
        return R.authenticationFail(e, e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R<Exception> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.error("handleUsernameNotFoundException -> Username not found! - {}", e.getMessage());
        return R.authenticationFail(e, e.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R<Exception> handleInvalidTokenException(InvalidTokenException e) {
        log.error("Invalid token! - {}", e.getMessage());
        return R.invalidToken(e);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<Exception> handleException(Exception e) {
        log.error("Uncaught exception! - msg: {}, e: {}", e.getMessage(), e);
        return R.fail(e.getMessage());
    }


    /**
     * 请求参数异常
     * @param e
     * @return
     */
    @ExceptionHandler(BadRequestParameterException.class)
    public R<Exception> handleBadRequestParameterException(BadRequestParameterException e) {
        log.error("Client请求参数错误 - msg: {}, e: {}", e.getMessage(), e);
        return R.badRequest();
    }

    @ExceptionHandler(BusinessException.class)
    public R<Exception> handleBusinessException(BusinessException e) {
        log.error("业务异常! - msg: {}, e: {}", e.getMessage(), e);
        return R.fail(e.getMessage(), e.getCode());
    }


}
