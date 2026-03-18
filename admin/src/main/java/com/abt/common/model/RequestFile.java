package com.abt.common.model;

import lombok.Data;

/**
 * 上传文件请求信息
 */
@Data
public class RequestFile {

    private String bizType;

    /**
     * 所属服务
     */
    private String service;

    /**
     * 上传文件名
     */
    private String fileName;
    /**
     * 关联ID1
     */
    private String relationId1;

    /**
     * 关联ID2
     *
     */
    private String relationId2;

    /**
     * 保存的目录
     */
    private String savedRoot;

}
