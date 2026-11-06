package com.abt.oa.controller;

import com.abt.common.model.R;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

/** 仅处理本模块参数绑定错误，业务异常继续使用全局处理器。 */
@RestControllerAdvice(assignableTypes = FieldVehicleController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class FieldVehicleParameterHandler {
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleBinding(BindException e) {
        String message = e.getAllErrors().stream().map(error -> error.getDefaultMessage()).distinct().collect(Collectors.joining("；"));
        log.error("野外用车参数校验失败: {}", message);
        return R.invalidParameters(message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleConstraint(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().map(v -> v.getMessage()).distinct().collect(Collectors.joining("；"));
        log.error("野外用车参数校验失败: {}", message);
        return R.invalidParameters(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleJson(HttpMessageNotReadableException e) {
        log.error("野外用车请求格式错误", e);
        return R.invalidParameters("请求格式错误，请检查日期、数值或JSON格式");
    }
}
