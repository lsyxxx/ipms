package com.abt.material.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

/**
 * 库存联合主键
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class InventoryId implements Serializable {

    @Column(name="m_id")
    private String materialId;
    @Column(name="wh_id")
    private String warehouseId;
}
