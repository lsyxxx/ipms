package com.abt.common.model;

import com.abt.sys.model.dto.UserView;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *
 */
@Data
@NoArgsConstructor
public class User implements Serializable {

    /**
     * 用户唯一ID
     */
    private String userid;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 编号，比如工号
     */
    private String code;

    public User(UserView user) {
        this.userid = user.getId();
        this.username = user.getUsername();
        this.code = user.getAccount();
    }


}
