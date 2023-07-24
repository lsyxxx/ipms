package com.ipms.sys.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.lang.Nullable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class User {

    private @Id Integer id;
    @Nullable
    private String userName;
    private String loginName;
    private String password;

    private String position;
    private String department;

    public static User of(String userName, String loginName, String password, String position, String department) {
        return new User(null, userName, loginName, password, position, department);
    }



}
