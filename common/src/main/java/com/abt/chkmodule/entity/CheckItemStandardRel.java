package com.abt.chkmodule.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 *  检测子参数-标准关系表
 */
@Getter
@Setter
@Entity
@Table(name = "check_item_std")
public class CheckItemStandardRel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name="ci_id", nullable = false)
    private String checkItemId;

    @Column(name="std_id", nullable = false)
    private String standardId;

    /**
     * 标准章节号
     */
    @Column(name="chapter_no")
    private String chapterNo;
}
