package com.abt.finance.invoice;

import com.abt.common.model.R;
import com.abt.finance.entity.Invoice;
import com.abt.finance.service.InvoiceService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.SystemErrorLog;
import com.abt.sys.service.SystemErrorLogService;
import com.abt.wf.model.WithInvoice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 发票校验切面，只能用于controller(存在@Controller或@ResetController注解)
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class InvoiceCheckControllerAspect {

    private final InvoiceService invoiceService;
    private final SystemErrorLogService systemErrorLogService;

    /**
     * 用于controller层
     */
    @Pointcut("within(com.abt.*.controller..*)")
    public void controllerLayer() {}

    /**
     * 发票校验注解切点
     */
    @Pointcut("@annotation(com.abt.finance.invoice.CheckAndSaveInvoice)")
    public void checkAndSaveInvoiceAnnotation() {}

    /**
     * 组合切点：controller层 + 发票校验注解
     */
    @Pointcut("controllerLayer() && checkAndSaveInvoiceAnnotation()")
    public void controllerWithInvoiceCheck() {}

    //内联切点
    @Before("controllerWithInvoiceCheck()")
    public void before(JoinPoint joinPoint) {
        log.debug("开始执行发票校验，方法: {}", joinPoint.getSignature().getName());
        
        // 获取方法参数
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof WithInvoice withInvoice) {
                InvoiceContext ctx = InvoiceContextHolder.getContext();
                List<Invoice> invoiceList = withInvoice.getInvoiceList();
                final List<Invoice> invoices = invoiceService.check(invoiceList);
                final List<String> errorList = invoices.stream().map(Invoice::getError).filter(StringUtils::isNotBlank).toList();
                if (!errorList.isEmpty()) {
                    ctx.setCheckResult(Boolean.FALSE);
                    StringBuilder errorMsg = new StringBuilder("发票查重失败：\n");
                    for (int i = 0; i < errorList.size(); i++) {
                        errorMsg.append(i + 1).append(". ").append(errorList.get(i)).append("\n");
                    }
                    throw new BusinessException(errorMsg.toString());
                }
                ctx.setInvoiceList(invoices);
                ctx.setCheckResult(Boolean.TRUE);
                log.debug("发票校验通过，发票数量: {}", invoices.size());
            }
        }
    }

    @AfterReturning(pointcut = "controllerWithInvoiceCheck() && @annotation(checkAndSaveInvoice)",
            returning = "result")
    public void afterReturn(Object result, CheckAndSaveInvoice checkAndSaveInvoice) {
        log.debug("方法执行成功，开始处理发票保存逻辑");
        if (!checkAndSaveInvoice.save()) {
            return;
        }
        InvoiceContext ctx = InvoiceContextHolder.getContext();
        List<Invoice> invoices = ctx.getInvoiceList();
        if (invoices != null && !invoices.isEmpty()) {
            String refCode = "";
            String refName = "";
            if (result instanceof R<?> r) {
                final Object data = r.getData();
                if (data instanceof WithInvoice withInvoice) {
                    refCode = withInvoice.getRefCode();
                    refName = withInvoice.getRefName();
                }
            }
            if (StringUtils.isBlank(refCode) || StringUtils.isBlank(refName)) {
                log.error("发票(invoiceService)保存：无法保存refCode/refName, invoiceList: {}", StringUtils.join(invoices, ","));
                try {
                    String msg = String.format("发票(invoiceService)保存：无法保存refCode/refName, invoice code:: %s",
                            invoices.stream().map(Invoice::getCode).collect(Collectors.joining(",")));
                    SystemErrorLog log = new SystemErrorLog();
                    log.setService(InvoiceService.SERVICE);
                    log.setError(msg);
                    systemErrorLogService.saveLog(log);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

            for (Invoice invoice : invoices) {
                invoice.setRefCode(refCode);
                invoice.setRefName(refName);
            }
            invoiceService.save(invoices);

        }
    }

    /**
     * 无论controller执行成功与否，都执行清理操作
     */
    @After("controllerWithInvoiceCheck()")
    public void after() {
        InvoiceContextHolder.clearContext();
    }

    

}
