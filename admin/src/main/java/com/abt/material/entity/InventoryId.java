package com.abt.material.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    @Column(name="m_id", nullable = false)
    private String materialId;
    @NotNull
    @Column(name="wh_id", nullable = false)
    private String warehouseId;
}
