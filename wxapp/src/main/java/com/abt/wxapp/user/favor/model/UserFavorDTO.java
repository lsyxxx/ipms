package com.abt.wxapp.user.favor.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

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
    private String name;

    /**
     * 封面图片URL
     */
    private String coverImage;

}