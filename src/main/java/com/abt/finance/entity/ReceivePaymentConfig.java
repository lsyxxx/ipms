package com.abt.finance.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import jakarta.persistence.*;

/**
 * 回款登记配置
 */
@Table(name = "fi_rec_payment_config")
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReceivePaymentConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 配置名称
     */
    @Column(name="name_")
    private String name;

    /**
     * 配置值
     */
    @Column(name="value_", columnDefinition = "VARCHAR(1000)")
    private String value;

    /**
     * 分类
     */
    @Column(name="type_")
    private String type;

    @Column(name="desc_")
    private String description;
}
