package com.abt.finance.model;

import com.abt.finance.entity.ReceivePayment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link com.abt.finance.entity.ReceivePayment}
 */
@Getter
@Setter
@NoArgsConstructor
public class ReceivePaymentDTO implements Serializable {
    private String id;
    private String createUserid;
    private String createUsername;
    private LocalDateTime createDate;
    private LocalDate receiveDate;
    private String remark;
    private Double amount;
    private String customerId;
    private String customerName;
    private String bankAccount;
    private String bankName;
    private String taxNo;
    private String notifyStrings;
    /**
     * 回款记录
     */
    private List<ReceivePayment> payments;

    /**
     * 收入说明
     */
    private String description;

    /**
     * 应收金额
     */
    private BigDecimal receivable;

    /**
     * 已回款金额
     */
    private BigDecimal registered;

    /**
     * 余额
     */
    private BigDecimal balance;

    public ReceivePaymentDTO(BigDecimal receivable, BigDecimal registered, BigDecimal balance) {
        this.receivable = receivable;
        this.registered = registered;
        this.balance = balance;
    }
}