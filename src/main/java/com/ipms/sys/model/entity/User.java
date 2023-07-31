package com.ipms.sys.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull;

@NoArgsConstructor
@Data
@ToString(exclude = {"password"})
@Schema(description = "用户实体")
public class User {

    @Schema(description = "主键ID")
    private @Id Long id;
    @NonNull
    @Schema(description = "用户名")
    private String userName;
    @NonNull
    @Schema(description = "登录名")
    private String loginName;
    @NonNull
    @Schema(description = "密码")
    private String password;

    @Schema(description = "职位")
    private String position;
    @Schema(description = "部门")
    private String department;
    /**
     * 状态
     */
    @Schema(description = "状态: 0正常, 1删除, 2封禁")
    private int status = Status.NORMAL.ordinal();

    public User(String userName, String loginName, String position, String department) {
        super();
        this.userName = userName;
        this.loginName = loginName;
        this.position = position;
        this.department = department;
    }

    public enum Status{
        NORMAL(0, "正常"),
        DELETE(1, "删除"),
        BAN(2, "封禁"),
        ;

        private final int index;
        private final String message;

        Status(int index, String message) {
            this.index = index;
            this.message = message;
        }
    }

}
