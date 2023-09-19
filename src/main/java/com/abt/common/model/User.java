package com.abt.common.model;

import com.abt.sys.model.dto.UserView;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 *
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class User implements Serializable {

    /**
     * 用户唯一ID
     */
    private String id;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 编号，比如工号
     */
    private String code;

    public User(UserView user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.code = user.getAccount();
    }

    public User(String id, String name) {
        this.id = id;
        this.username = name;
    }

    public User(String id, String name, String code) {
        this.id = id;
        this.username = name;
        this.code = code;
    }


}
