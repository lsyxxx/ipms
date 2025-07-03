package com.abt.market.model;

import com.abt.sys.model.entity.SystemFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用于列表展示结算主要信息，防止controller返回json序列化导致懒加载问题
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementMainListDTO{
    
    /**
     * 结算单号
     */
    private String id;
    
    /**
     * 结算客户id
     */
    private String clientId;
    
    /**
     * 结算客户name
     */
    private String clientName;
    
    /**
     * 收款公司名称
     */
    private String companyName;
    
    /**
     * 收款公司税号
     */
    private String taxNo;
    
    /**
     * 收款公司电话
     */
    private String telephoneNo;
    
    /**
     * 收款公司开户行
     */
    private String accountBank;
    
    /**
     * 收款公司账户
     */
    private String accountNo;
    
    /**
     * 是否含税
     */
    private boolean isTax;
    
    /**
     * 税率
     */
    private Double taxRate;
    
    /**
     * 优惠百分比
     */
    private Double discountPercentage;
    
    /**
     * 优惠金额
     */
    private Double discountAmount;
    
    /**
     * 本次结算总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 保存类型（暂存/保存）
     */
    private SaveType saveType;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 附件
     */
    private List<SystemFile> attachments;
    
    // 审计字段
    /**
     * 创建时间
     */
    private LocalDateTime createDate;
    
    /**
     * 创建人
     */
    private String createUserid;
    private String createUsername;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateDate;
    
    /**
     * 更新人
     */
    private String updateUserid;
    private String updateUsername;
}
