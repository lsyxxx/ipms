package com.abt.filecenter.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 单文件上传表单，不允许客户端指定桶和完整对象键。
 */
@Getter
@Setter
public class FileUploadRequest {

    @NotNull(message = "请选择要上传的文件")
    private MultipartFile file;

    @Pattern(regexp = "[A-Za-z0-9_-]{1,64}", message = "文件目录只能包含字母、数字、中划线和下划线，长度为1到64位")
    private String directory = "uploads";
}
