package com.abt.sys.exception;

import com.abt.common.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.security.access.AccessDeniedException;
import java.util.stream.Collectors;

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
        log.error("Authentication failed! - ", e);
        return R.authenticationFail(e, e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R<Exception> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.error("handleUsernameNotFoundException -> Username not found! - ", e);
        return R.authenticationFail(e, e.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R<Exception> handleInvalidTokenException(InvalidTokenException e) {
        log.error("Invalid token! ", e);
        return R.invalidToken(e);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Exception> handleException(Exception e) {
        log.error("Uncaught exception! - ", e);
        return R.fail(e.getMessage());
    }


    /**
     * 请求参数异常
     * @param e 异常
     */
    @ExceptionHandler(BadRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<BadRequestParameterException> handleBadRequestParameterException(BadRequestParameterException e) {
        log.error("Client请求参数错误 - ", e);
        return R.badRequest(e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Exception> handleBusinessException(BusinessException e) {
        log.error("业务异常! - ", e);
        return R.fail(e.getMessage(), e.getCode());
    }


    @ExceptionHandler(SystemFileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<SystemFileNotFoundException> handleSystemFileNotFoundException(SystemFileNotFoundException e) {
        log.error("文件未找到! - ", e);
        return R.fileNotFound();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Exception> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("参数校验失败: ", e);
        BindingResult bindingResult = e.getBindingResult();
        String messages = bindingResult.getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("；"));
        return R.invalidParameters(messages);
    }

    /**
     * 文件上传大小异常
     */
    @ExceptionHandler({MaxUploadSizeExceededException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Exception> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("上传文件大小超出范围: ", e);
        return R.fail("上传文件大小超出范围!(单个文件大小不能超过50MB,一次上传所有文件大小不能超过200MB");
    }


    /**
     * controller没有接收到必填参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public void handleMissingParams(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        throw new BusinessException("缺少参数: " + name);
    }


    /**
     * 访问拒绝（无授权）
     */
    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(AccessDeniedException ex) {
        log.error(ex.getMessage(), ex);
        throw new BusinessException("无权访问或操作");
    }
}
