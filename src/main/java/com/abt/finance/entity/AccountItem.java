package com.abt.finance.entity;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.AuditInfo;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.WithQuery;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 科目
 */
@Getter
@Setter
@Entity
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@Table(name = "fi_acc_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
public class AccountItem extends AuditInfo implements Comparable<AccountItem>{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull(message = "科目名称不能为空", groups = {ValidateGroup.Save.class})
    @Column(name="name_", length = 50)
    private String name;

    @Column(name="level_", columnDefinition="TINYINT")
    private Integer level;

    /**
     * 科目编码
     */
    @NotNull(message = "科目编码不能为空", groups = {ValidateGroup.Save.class})
    @Column(name="code_", columnDefinition="VARCHAR(128)")
    private String code;
    /**
     * 科目类型
     */
    @NotNull(message = "科目类型", groups = {ValidateGroup.Save.class})
    @Column(name="type_", columnDefinition="VARCHAR(32)")
    private String type;
    /**
     * 借贷方向
     */
    @Column(name="direction_", columnDefinition="VARCHAR(16)")
    private String direction;

    /**
     * 是否启用
     */
    @Column(name="enabled_", columnDefinition = "BIT")
    private boolean enabled = true;
    /**
     * 说明
     */
    @Column(name="remark_", columnDefinition="VARCHAR(128)")
    private String remark;

    /**
     * 级联名称
     */
    @Column(name="cascade_", columnDefinition = "VARCHAR(512)")
    private String cascade;

    @Transient
    private long sort;



    /**
     * 分类层级，0
     */
    public static final int LEVEL_TYPE = 0;

    /**
     * 创建科目（非分类项）
     */
    public static AccountItem createItem(int level, String code, String name, String type) {
        if (level == 0) {
            throw new BusinessException("财务科目层级不能为0");
        }
        Assert.hasText(code, "财务科目编码不能为空");
        Assert.hasText(name, "财务科目名称不能为空");
        Assert.hasText(type, "财务科目分类不能为空");
        AccountItem item = new AccountItem();
        item.setLevel(level);
        item.setCode(code);
        item.setName(name);
        item.setType(type);
        return item;
    }

    /**
     * 比较2个科目排序
     * @param a1 科目1
     * @param a2 科目2
     */
    public static int compare(AccountItem a1, AccountItem a2) {
        String code1 = a1.getCode();
        String code2 = a2.getCode();
        if (code1.equals(code2)) {
            return 0;
        }
        int len = Math.max(code1.length(), code2.length());
        code1 = String.format("%-" + len + "s", code1).replace(' ', '0');
        code2 = String.format("%-" + len + "s", code2).replace(' ', '0');
        try {
            return Long.parseLong(code1) - Long.parseLong(code2) > 0 ? 1 : -1;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("科目编码格式错误!");
        }
    }

    @Override
    public int compareTo(AccountItem o) {
        Assert.notNull(o, "Parameter: AccountItem cannot be null");
        return compare(this, o);
    }
}