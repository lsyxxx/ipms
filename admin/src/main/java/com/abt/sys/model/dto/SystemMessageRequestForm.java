package com.abt.sys.model.dto;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 */
@Getter
@Setter
public class SystemMessageRequestForm extends RequestForm {
    private List<String> typeIds;
    private String toId;
    private Integer toStatus;
    /**
     * 查询生产任务
     */
    private boolean isTestTask = false;

    /**
     * 是否查询当前登录用户
     */
    public boolean isLoginUser() {
        return ToId_LoginUser.equals(toId);
    }

    public List<String> buildTypeIds() {
        if (this.typeIds == null || this.typeIds.isEmpty()) {
            this.typeIds = List.of("all");
        }
        return this.typeIds;
    }

    public static String ToId_LoginUser = "loginUser";

}
