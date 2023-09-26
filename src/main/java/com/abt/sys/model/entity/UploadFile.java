package com.abt.sys.model.entity;

import com.abt.common.util.FileUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 上传文件
 */

@Data
@Schema(description = "UploadFile上传文件表,来源OPENAUTH")
@Table(name = "UploadFile")
@Comment("上传文件表")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class UploadFile implements Serializable {

    public static final long serialVersionUID = -1988249215026734302L;

    @Id
    @Column(name = "Id", columnDefinition = "PrimaryKey")
    private String id;

    @Column(name = "FileName", columnDefinition = "VARCHAR(200)")
    private String fileName;

    @Column(name = "FilePath", columnDefinition = "VARCHAR(500)")
    private String filePath;

    @Column(name = "Description", columnDefinition = "VARCHAR(200)")
    private String description;

    /**
     * 附件类型
     */
    @Column(name = "FileType", columnDefinition = "PrimaryKey")
    private String fileType;

    @Column(name = "FileSize", columnDefinition = "INT")
    private long fileSize;

    @Column(name = "Enable", columnDefinition = "BIT NOT NULL DEFAULT 1")
    private boolean enable = true;

    @Column(name = "SortCode", columnDefinition = "INT NOT NULL DEFAULT 0")
    private int sortCode = 0;

    @Column(name = "Extension", columnDefinition = "VARCHAR(20)")
    private String extension;

    @Column(name = "DeleteMark", columnDefinition = "BIT NOT NULL DEFAULT 0")
    private boolean deleteMark = false;

    @CreatedBy
    @Column(name = "CreateUserId", columnDefinition = "uniqueidentifier")
    private String createUserId;

    @Column(name = "CreateUserName", columnDefinition = "VARCHAR(50)")
    private String createUserName;

    @CreatedDate
    @Column(name = "CreateTime", columnDefinition = "DATETIME")
    private LocalDateTime createTime;

    @Column(name = "Thumbnail", columnDefinition = "VARCHAR(500)")
    private String thumbnail;

    @Column(name = "BelongApp", columnDefinition = "VARCHAR(200)")
    private String belongApp;

    @Column(name = "BelongAppId", columnDefinition = "VARCHAR(50)")
    private String belongAppId;


    public static UploadFile create(MultipartFile file, String path) {
        UploadFile uploadFile = new UploadFile();
        uploadFile.setFileName(file.getOriginalFilename());
        uploadFile.setFilePath(path);
        uploadFile.setFileSize(file.getSize());
        //保存票据类型
        uploadFile.setFileType(FileUtil.getFileExtension(file.getOriginalFilename()));


        return uploadFile;

    }



}
