package com.abt.sys.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Org {
    private String id;

    private String cascadeId;

    private String name;

    private String hotKey;

    private String parentName;

    private Boolean isLeaf = false;

    private Boolean isAutoExpand = false;

    private String iconName;

    private int status;

    private String bizCode;

    private String customCode;

    private LocalDateTime createTime;

    private int createId;

    private int sortNo;

    private String typeName;

    private String parentId;

    private String typeId;



}