package com.abt.sys.model.entity;

import com.abt.common.model.AuditInfo;
import com.abt.common.model.RequestFile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件系统
 */


@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SystemFile extends AuditInfo {

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

    private boolean isDeleted = false;


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
