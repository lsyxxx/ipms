package com.ipms.sys.model.dto;

import com.ipms.sys.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull;

/**
 * UserDTO
 */
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {

    private @Id Long id;
    @NonNull
    private String userName;
    @NonNull
    private String loginName;
    private String position;
    private String department;

    public UserDTO(User user) {
        this.id = user.getId();
        this.userName = user.getUserName();
        this.loginName = user.getLoginName();
        this.position = user.getPosition();
        this.department = user.getDepartment();
    }

}
