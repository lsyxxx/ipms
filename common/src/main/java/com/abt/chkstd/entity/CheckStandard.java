package com.abt.chkstd.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;


/**
 * 检测标准
 */
@Getter
@Setter
@Entity
@Table(name = "T_check_BZ")
public class CheckStandard {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    private String id;

    @Size(max = 50)
    @Column(name = "BzType", length = 50)
    private String bzType;

    @Size(max = 500)
    @Column(name = "BzCode", length = 500)
    private String bzCode;

    @Size(max = 100)
    @NotNull
    @Column(name = "BzName", nullable = false, length = 100)
    private String bzName;

    @Size(max = 500)
    @Column(name = "BzNote", length = 500)
    private String bzNote;

    @Size(max = 2)
    @Column(name = "isFlag", length = 2)
    private String isFlag;

    @Size(max = 2)
    @Column(name = "biaozhunLevel", length = 2)
    private String biaozhunLevel;

    @Column(name = "fabudate")
    private LocalDateTime fabudate;

    @Column(name = "shishidate")
    private LocalDateTime shishidate;

    @Lob
    @Column(name = "filePath")
    private String filePath;

    @Size(max = 2)
    @Column(name = "Isactive", length = 2)
    private String isactive;

    @Size(max = 200)
    @Column(name = "note", length = 200)
    private String note;

    /**
     * 标准现行
     */
    public static final String ACTIVE_RUNNING = "1";

    /**
     * 标准已过期
     */
    public static final String ACTIVE_FAKE = "0";

    /**
     * 标准正在实行
     */
    public void setActive() {
        this.isactive = ACTIVE_RUNNING;
    }

    /**
     * 标准废弃
     */
    public void setFake() {
        this.isactive = ACTIVE_FAKE;
    }

}