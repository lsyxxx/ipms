package com.abt.chemicals.entity;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.AuditInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 化学品
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "chm_product")
@Accessors(chain = true)
@DynamicInsert
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Product extends AuditInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "化学品名称不能为空!", groups = {ValidateGroup.Save.class})
    private String name;
    @NotBlank(message = "化学品应用类别不能为空", groups = {ValidateGroup.Save.class})
    private String type1;
    @NotBlank(message = "化学品功能类别不能为空", groups = {ValidateGroup.Save.class})
    private String type2;

    @Column(columnDefinition="VARCHAR(1000)")
    private String usage;
    
    @Column(columnDefinition="VARCHAR(1000)")
    private String manufacturing;

    @Transient
    private List<Standard> standards = new ArrayList<>();

    @Transient
    private List<Material> mainMaterial = new ArrayList<>();

    @Transient
    private List<Material> auxMaterial = new ArrayList<>();

    @Transient
    private List<Company> producers = new ArrayList<>();

    @Transient
    private List<Company> buyers = new ArrayList<>();

}
