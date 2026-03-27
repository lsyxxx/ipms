package com.abt.wxapp.user.favor.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户收藏列表DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserFavorDTO {

    // 1. 收藏记录本身的属性 (来自 OpenUserFavor 表)
    /**
     * 收藏记录主键
     */
    private String favorId;

    /**
     * 收藏时间
     */
    private LocalDateTime createDate;


    // 2. 检测项目的展示属性 (来自 CheckModule 表)
    /**
     * 检测项目ID
     */
    private String checkModuleId;

    /**
     * 检测项目名称
     */
    private String checkModuleName;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 价格
     */
    private String price;

    /**
     * 检测时间/一般工作时间
     */
    private String duration;

    /**
     * 认证信息 (前端要求必须是数组: ['CMA', 'CNAS'])
     */
    private List<String> certificate;

    /**
     * 是否启用状态
     */
    private boolean active;



    // 🎯 专供 JPQL 调用的构造函数：直接接收 boolean 类型的 hasCma 和 hasCnas
    public UserFavorDTO(String favorId, LocalDateTime createDate, String checkModuleId,
                        String checkModuleName, String coverImage, String price,
                        String duration, boolean active, boolean hasCma, boolean hasCnas) {
        this.favorId = favorId;
        this.createDate = createDate;
        this.checkModuleId = checkModuleId;
        this.checkModuleName = checkModuleName;
        this.coverImage = coverImage;
        this.price = price;
        this.duration = duration;
        this.active = active;

        List<String> certs = new ArrayList<>();
        if (hasCma) certs.add("CMA");
        if (hasCnas) certs.add("CNAS");
        this.certificate = certs;
    }
}