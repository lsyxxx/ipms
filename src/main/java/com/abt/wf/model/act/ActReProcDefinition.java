package com.abt.wf.model.act;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@Table(name = "ACT_RE_PROCDEF")
public class ActReProcDefinition {
    @Id
    @Size(max = 64)
    @Nationalized
    @Column(name = "ID_", nullable = false, length = 64)
    private String id;

    @Column(name = "REV_")
    private Integer rev;

    @Size(max = 255)
    @Nationalized
    @Column(name = "CATEGORY_")
    private String category;

    @Size(max = 255)
    @Nationalized
    @Column(name = "NAME_")
    private String name;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "KEY_", nullable = false)
    private String key;

    @NotNull
    @Column(name = "VERSION_", nullable = false)
    private Integer version;

    @Size(max = 64)
    @Nationalized
    @Column(name = "DEPLOYMENT_ID_", length = 64)
    private String deploymentId;

    @Size(max = 4000)
    @Nationalized
    @Column(name = "RESOURCE_NAME_", length = 4000)
    private String resourceName;

    @Size(max = 4000)
    @Nationalized
    @Column(name = "DGRM_RESOURCE_NAME_", length = 4000)
    private String dgrmResourceName;

    @Column(name = "HAS_START_FORM_KEY_", columnDefinition = "tinyint")
    private Short hasStartFormKey;

    @Column(name = "SUSPENSION_STATE_", columnDefinition = "tinyint")
    private Short suspensionState;

    @Size(max = 64)
    @Nationalized
    @Column(name = "TENANT_ID_", length = 64)
    private String tenantId;

    @Size(max = 64)
    @Nationalized
    @Column(name = "VERSION_TAG_", length = 64)
    private String versionTag;

    @Column(name = "HISTORY_TTL_")
    private Integer historyTtl;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "STARTABLE_", nullable = false)
    private Boolean startable = false;

}