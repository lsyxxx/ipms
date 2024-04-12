package com.abt.chemicals.entity;

import com.abt.common.model.AuditInfo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 化学品-厂商关联表
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Table(name = "chm_com_rel")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class CompanyRelation extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name="chm_id", columnDefinition="VARCHAR(128)")
    private String chemicalId;
    @Column(name="com_id", columnDefinition="VARCHAR(128)")
    private String companyId;
    
    @Column(name="com_type", columnDefinition="VARCHAR(128)")
    private String companyType;

    @Column(name="chm_code", columnDefinition="VARCHAR(128)")
    private String chemicalCode;

    public static CompanyRelation of(String chemicalId, Company company) {
        CompanyRelation rel = new CompanyRelation();
        rel.setChemicalId(chemicalId).setCompanyId(company.getId()).setCompanyType(company.getType());
        return rel;
    }


}
