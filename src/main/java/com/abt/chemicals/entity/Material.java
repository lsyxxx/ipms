package com.abt.chemicals.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 化学品材料
 */
@Data
@Table(name = "chm_material")
@Accessors(chain = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    /**
     * main/aux
     * 主要/辅助
     */
    private String type;
    private String chemicalId;

    /**
     * 主材料
     */
    public static final String TYPE_MAIN = "main";
    /**
     * 辅助材料
     */
    public static final String TYPE_AUX = "aux";

    public static Material of(String name, String type, String chemicalId) {
        Material m = new Material();
        m.setName(name).setType(type).setChemicalId(chemicalId);
        return m;
    }
}
