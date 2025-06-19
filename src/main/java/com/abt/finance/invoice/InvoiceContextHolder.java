package com.abt.finance.invoice;

import com.abt.finance.entity.Invoice;

import java.util.List;

/**
 *
 */
public class InvoiceContextHolder {

    private static final ThreadLocal<InvoiceContext> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 获取当前上下文
     */
    public static InvoiceContext getContext() {
        InvoiceContext context = CONTEXT_HOLDER.get();
        if (context == null) {
            context = new InvoiceContext();
            CONTEXT_HOLDER.set(context);
        }
        return context;
    }

    /**
     * 设置上下文
     */
    public static void setContext(InvoiceContext context) {
        if (context == null) {
            clearContext();
        } else {
            CONTEXT_HOLDER.set(context);
        }
    }

    /**
     * 清空上下文
     */
    public static void clearContext() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * 创建新的上下文
     */
    public static InvoiceContext createContext(List<Invoice> invoiceList, Object relatedEntity) {
        InvoiceContext context = new InvoiceContext(invoiceList, relatedEntity);
        setContext(context);
        return context;
    }

}
