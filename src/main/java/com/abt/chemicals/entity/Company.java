package com.abt.chemicals.entity;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.AuditInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 公司信息
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "chm_company")
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@Accessors(chain = true)
public class Company extends AuditInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @NotBlank(message = "id不能为空", groups = {ValidateGroup.Update.class})
    private String id;

    /**
     * 全称（正式）
     */
    @Column(name="full_name", columnDefinition="VARCHAR(128)")
    private String fullName;

    @NotBlank(message = "单位名称不能为空!", groups = {ValidateGroup.Save.class})
    @Column(name="name_", columnDefinition="VARCHAR(128)")
    private String name;

    @Column(name="address_", columnDefinition="VARCHAR(256)")
    private String address;

    @NotBlank(message = "type不能为空", groups = {ValidateGroup.Save.class})
    @Column(name="type_", columnDefinition="VARCHAR(128)")
    private String type;

    @Column(name="enable_", columnDefinition="BIT")
    private Boolean enable;

    @Column(name="sort_", columnDefinition="TINYINT")
    @Max(value = 255, message = "序号最大不能超过255", groups = {ValidateGroup.All.class})
    private Integer sort;
    /**
     * 备注
     */
    @Column( columnDefinition="VARCHAR(1000)")
    private String note;

    /**
     * 营业执照附件id
     */
    @Transient
    private List<String> licenseList = new ArrayList<>();

    public static final String TYPE_BUYER = "buyer";
    public static final String TYPE_PRODUCER = "producer";

    public static boolean validateType(String type) {
        if (StringUtils.isBlank(type)) {
            return false;
        }
        if (TYPE_BUYER.equals(type) || TYPE_PRODUCER.equals(type)) {
            return true;
        }
        return false;
    }

    @Transient
    private List<Contact> contactList = new ArrayList<>();
    @Transient
    private List<Price> priceList = new ArrayList<>();

    public Company prepare() {
        if (Objects.isNull(this.enable)) {
            this.enable = true;
        }
        if (Objects.isNull(this.sort)) {
            this.sort = 1;
        }
        return this;
    }

    public static Company create() {
        Company company = new Company();
        company.prepare();
        return company;
    }

    public static Company condition() {
        return new Company();
    }

}
