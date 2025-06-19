package com.abt.finance.invoice;

import com.abt.finance.entity.Invoice;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 发票context
 */
@Getter
@Setter
public class InvoiceContext {
    /**
     * 发票列表
     */
    private List<Invoice> invoiceList;

    /**
     * 查重结果
     */
    private Boolean checkResult;

    /**
     * 关联业务实体id
     */
    private String refCode;

    /**
     * 关联业务类型
     */
    private String refName;

    /**
     * 关联业务实体
     */
    private Object relatedEntity;

    /**
     * 关联的业务实体类型
     */
    private Class<?> relatedEntityClass;

    /**
     * 清除context
     */
    public void clearContext() {
        this.invoiceList.clear();
        this.refCode = null;
        this.relatedEntity = null;
    }

    public InvoiceContext() {
        this.invoiceList = new ArrayList<>();
    }

    public InvoiceContext(List<Invoice> invoiceList) {
        this.invoiceList = invoiceList != null ? new ArrayList<>(invoiceList) : new ArrayList<>();
    }

    public InvoiceContext(List<Invoice> invoiceList, Object relatedEntity) {
        this(invoiceList);
        this.relatedEntity = relatedEntity;
    }
    public InvoiceContext(List<Invoice> invoiceList, Object relatedEntity, Class<?> relatedEntityClass) {
        this(invoiceList);
        this.relatedEntity = relatedEntity;
        this.relatedEntityClass = relatedEntityClass;
    }

}
