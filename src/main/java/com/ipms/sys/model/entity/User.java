package com.ipms.sys.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull;

@NoArgsConstructor
@Data
@ToString(exclude = {"password"})
public class User {

    private @Id Long id;
    @NonNull
    private String userName;
    @NonNull
    private String loginName;
    @NonNull
    private String password;

    private String position;
    private String department;
    /**
     * 状态
     */
    private int status = Status.NORMAL.ordinal();

    public User(String userName, String loginName, String position, String department) {
        super();
        this.userName = userName;
        this.loginName = loginName;
        this.position = position;
        this.department = department;
    }

    enum Status{
        NORMAL(0, "正常"),
        DELETE(1, "删除"),
        VAC(2, "封禁"),
        ;

        Status(int i, String 正常) {
        }
    }

}
