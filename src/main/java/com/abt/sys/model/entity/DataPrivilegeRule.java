package com.abt.sys.model.entity;

import com.abt.common.util.JsonUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.WithQuery;
import com.abt.sys.model.dto.DataRule;
import com.abt.sys.model.dto.UserView;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.naming.AuthenticationNotSupportedException;
import java.time.Instant;

@Getter
@Setter
@Entity
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataPrivilegeRule implements WithQuery<DataPrivilegeRule> {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    private String id;

    @Size(max = 50)
    @NotNull
    @Column(name = "SourceCode", nullable = false, length = 50)
    private String sourceCode;

    @Size(max = 50)
    @Column(name = "SubSourceCode", length = 50)
    private String subSourceCode;

    @Size(max = 255)
    @NotNull
    @Column(name = "Description", nullable = false)
    private String description;

    @NotNull
    @Column(name = "SortNo", nullable = false)
    private Integer sortNo;


    /**
     * 权限规则，json
     */
    @Size(max = 1000)
    @NotNull
    @Column(name = "PrivilegeRules", nullable = false, length = 1000)
    private String privilegeRules;

    @NotNull
    @Column(name = "Enable", nullable = false)
    private Boolean enable = false;

    @NotNull
    @Column(name = "CreateTime", nullable = false)
    private Instant createTime;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserId", nullable = false, length = 50)
    private String createUserId;

    @Size(max = 200)
    @NotNull
    @Column(name = "CreateUserName", nullable = false, length = 200)
    private String createUserName;

    @Column(name = "UpdateTime")
    private Instant updateTime;

    @Size(max = 50)
    @Column(name = "UpdateUserId", length = 50)
    private String updateUserId;

    @Size(max = 200)
    @Column(name = "UpdateUserName", length = 200)
    private String updateUserName;

    /**
     * 权限对象，privilegeRules json转对象
     */
    @Transient
    private DataRule rule;

    @Override
    public DataPrivilegeRule afterQuery() {
        try {
            this.rule = DataRule.create(this.privilegeRules);
            return this;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("获取权限控制失败！ - " + e.getMessage());
        }
    }

    public boolean checkRule(UserView userView) {
        if (rule == null) {
            //没有权限控制
            return true;
        }
        return this.rule.doFilterChain(userView);
    }
}