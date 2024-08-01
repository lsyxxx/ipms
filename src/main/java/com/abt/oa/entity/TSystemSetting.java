package com.abt.oa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "T_SystemSetting")
public class TSystemSetting {
    @Size(max = 50)
    @NotNull
    @Column(name = "Id", nullable = false, length = 50)
    @Id
    private String id;

    @Size(max = 50)
    @NotNull
    @Column(name = "ftypeid", nullable = false, length = 50)
    private String ftypeid;

    @Size(max = 30)
    @Column(name = "Fvalue", length = 30)
    private String fvalue;

}