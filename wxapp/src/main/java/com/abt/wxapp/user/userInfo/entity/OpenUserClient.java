package com.abt.wxapp.user.userInfo.entity;

import com.abt.common.config.ValidateGroup;
import com.abt.wxapp.db.AuditInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@Table(name = "open_user_client")
@DynamicUpdate
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners({AuditingEntityListener.class})
public class OpenUserClient extends AuditInfo {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", length = 50)
    private String userId;

    @NotNull(message = "委托方名称不能为空", groups = {ValidateGroup.Save.class})
    @Column(name = "client_name", length = 100)
    private String clientName;

    @NotNull(message = "联系人姓名不能为空", groups = {ValidateGroup.Save.class})
    @Column(name = "contact_name", length = 100)
    private String contactName;

    @NotNull(message = "联系人电话不能为空", groups = {ValidateGroup.Save.class})
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;


}
