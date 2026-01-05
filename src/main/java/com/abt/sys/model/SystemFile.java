package com.abt.sys.model;

import com.abt.common.model.AuditInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件系统
 */
@ToString(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Setter
public class SystemFile {

    private String id;

    private String name;

    private String originalName;

    private String description;

    private String url;

    /**
     * 保存文件的完整地址: url/fileName
     */
    private String fullPath;

    private String type;

    private String service;

    private String bizType;

    private String relationId1;

    private String relationId2;

    private String uid;

    private String status;

    private boolean isDeleted = false;

    private String base64;

    public SystemFile(MultipartFile file, String service, String filePath, Boolean withTime) {
        super();
        this.id = UUID.randomUUID().toString();
        this.originalName = file.getOriginalFilename();
        this.name = file.getOriginalFilename();
        this.service = service;
        if (withTime != null && withTime) {
            this.url = this.createPath(filePath);
        } else {
            this.url = this.createPath2(filePath);
        }
        this.fullPath = this.url + file.getOriginalFilename();
    }


    public SystemFile(String originalName, String service, String filePath, Boolean withTime) {
        super();
        this.id = UUID.randomUUID().toString();
        this.originalName = originalName;
        this.name = originalName;
        this.service = service;
        if (withTime != null && withTime) {
            this.url = this.createPath(filePath);
        } else {
            this.url = this.createPath2(filePath);
        }
        this.fullPath = this.url + originalName;
    }

    public SystemFile rename(String newName) {
        this.setName(newName);
        this.setFullPath(this.url + newName);
        return this;
    }


    /**
     * 路径规则：root/service/日期(20231011格式)/relationId1(ifExists)/relationId2(ifExists)
     * @return url
     */
    public String createPath(String root) {
        String path = root + File.separator + service + File.separator + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + File.separator;
        if (StringUtils.hasLength(this.relationId1)) {
            path = path + relationId1 + File.separator;
        }
        if (StringUtils.hasLength(this.relationId2)) {
            path = path + relationId2 + File.separator;
        }
        this.url = path;
        return path;
    }
    public String createPath2(String root) {
        return  root + File.separator + service + File.separator;
    }


}
