package com.ipms.sys.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class User {

    private @Id Integer id;
    @NonNull
    private String userName;
    @NonNull
    private String loginName;
    @NonNull
    private String password;

    private String position;
    private String department;

    /**
     * New from properties
     * @param userName
     * @param loginName
     * @param password
     * @param position
     * @param department
     * @return
     */
    public static User of(String userName, String loginName, String password, String position, String department) {
        return new User(null, userName, loginName, password, position, department);
    }

    /**
     * Copy properties
     * @param user
     * @return
     */
    public static User of(User user) {
        return new User(user.getId(), user.getUserName(), user.getLoginName(), user.getPassword(), user.getPosition(), user.getDepartment());
    }

}
