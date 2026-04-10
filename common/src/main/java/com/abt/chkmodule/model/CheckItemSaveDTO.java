package com.abt.chkmodule.model;

import lombok.Data;
import java.util.List;

/**
 * 负责接收前端 保存/编辑 参数
 */
@Data
public class CheckItemSaveDTO {

    /**
     * 子参数主键ID
     */
    private String id;

    /**
     * 所属检测项目ID
     */
    private String checkModuleId;

    /**
     * 检测子参数名称
     */
    private String name;

    /**
     * 检测子参数代码
     */
    private String code;

    /**
     * 简要描述
     */
    private String description;

    /**
     * 其他常用名称
     */
    private String aliasName;

    /**
     * 限制范围说明
     */
    private String restrict;

    /**
     * 是否启用（true: 启用, false: 停用）
     */
    private boolean enabled;

    /**
     * 是否有CMA资质 (true: 有, false: 无)
     */
    private boolean isCma;

    /**
     * 是否有CNAS资质 (true: 有, false: 无)
     */
    private boolean isCnas;

    /**
     * 其他资质证书说明（如果有多个，可用逗号分隔）
     */
    private String otherCertificate;

    /**
     * 关联的检测标准ID集合（纯粹的标准ID数组）
     */
    private List<String> standardIds;
}