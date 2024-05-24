package com.abt.testing.entity;

import com.abt.common.config.ValidateGroup;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;


/**
 * 枚举类
 */
@Getter
@Setter
@Entity
@Table(name = "T_ENUMLIB")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnumLib {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "ID", nullable = false, length = 50, unique = true)
    @NotNull(message = "ID不能为空", groups = {ValidateGroup.Save.class})
    private String id;

    /**
     * 枚举项
     */
    @NotNull(message = "FID不能为空", groups = {ValidateGroup.Save.class})
    @Size(max = 50)
    @Column(name = "FID", length = 50)
    private String fid;


    /**
     * 分类id
     */
    @NotNull(message = "FTYPEID不能为空", groups = {ValidateGroup.Save.class})
    @Size(max = 50)
    @Nationalized
    @Column(name = "FTYPEID", nullable = false, length = 50)
    private String ftypeid;

    /**
     * 名称
     */
    @NotNull(message = "FNAME不能为空", groups = {ValidateGroup.Save.class})
    @Size(max = 50)
    @Nationalized
    @Column(name = "FNAME", nullable = false, length = 50)
    private String fname;

    @NotNull(message = "FDESC不能为空", groups = {ValidateGroup.Save.class})
    @Size(max = 50)
    @Nationalized
    @Column(name = "FDESC", nullable = false, length = 50)
    private String fdesc;

    @Size(max = 18)
    @Nationalized
    @Column(name = "OPERATOR", length = 18)
    private String operator;

    @Column(name = "OPERATEDATE", columnDefinition = "DATETIME")
    private LocalDateTime operateDate;

    @Size(max = 13)
    @Nationalized
    @Column(name = "OPERATEDEPT", length = 13)
    private String operateDept;

    /**
     * 扩展, json保存
     */
    @Column(name = "EXTEND")
    private String extend;


}