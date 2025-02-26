package com.abt.wf.model;

import com.abt.common.util.MoneyUtil;
import cn.idev.excel.metadata.data.WriteCellData;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * excel导出
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReimburseExportDTO {
    private String id;
    private String company;
    private String createUsername;
    private String createDate;
    private String deptName;
    private String teamName;
    private String payLevel;
    private String project;
    private Double cost;
    /**
     * 费用金额大写
     */
    private String costUpperCase;
    /**
     * 应付款
     */
    private Double paying;
    private String payingUpperCase;
    private Integer voucherNum;
    private Integer attachmentNum;
    private Double loan;
    private String loanUpperCase;
    private String reason;
    private String payDate;
    private String payType;
    private String receiveUser;

    private String managerId;
    private String managerComment;
    private String managerDate;
    private String leaderId;
    private String leaderComment;
    private String leaderDate;
    private String acctId;
    private String acctComment;
    private String acctDate;
    private String finManagerId;
    private String finManagerComment;
    private String finManagerDate;
    private String ceoId;
    private String ceoComment;
    private String ceoDate;
    private String chiefId;
    private String chiefComment;
    private String chiefDate;
    private String cashierId;
    private String cashierComment;
    private String cashierDate;

    private String managerDef;
    private String leaderDef;
    private String accountDef;
    private String finManagerDef;
    private String ceoDef;
    private String chiefDef;
    private String cashierDef;
    private String multiMgrDef;

    private WriteCellData<Void> managerSig;
    private WriteCellData<Void> leaderSig;
    private WriteCellData<Void> acctSig;
    private WriteCellData<Void> finManagerSig;
    private WriteCellData<Void> ceoSig;
    private WriteCellData<Void> chiefSig;
    private WriteCellData<Void> cashierSig;


    public ReimburseExportDTO(String multiMgrDef, String managerDef, String leaderDef, String accountDef, String finManagerDef, String ceoDef, String chiefDef, String cashierDef) {
        this.multiMgrDef = multiMgrDef;
        this.managerDef = managerDef;
        this.leaderDef = leaderDef;
        this.accountDef = accountDef;
        this.finManagerDef = finManagerDef;
        this.ceoDef = ceoDef;
        this.chiefDef = chiefDef;
        this.cashierDef = cashierDef;
    }

    public void upperCase() {
        if (cost != null) {
            this.costUpperCase = MoneyUtil.toUpperCase(cost.toString());
        }
        if (loan != null) {
            this.loanUpperCase = MoneyUtil.toUpperCase(loan.toString());
        }
        if (paying != null) {
            this.payingUpperCase = MoneyUtil.toUpperCase(paying.toString());
        }
    }

}
