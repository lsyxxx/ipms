package com.abt.finance.invoice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 校验和保存发票
 * 1. 业务实体类需实现WithInvoice
 * 2. 只能用于controller方法上
 * 3. 若需要保存发票，controller需返回业务实体eg: R.success(entity)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckAndSaveInvoice {

    /**
     * 是否需要保存发票到发票库中
     * 默认保存
     */
    boolean save() default true;

}
