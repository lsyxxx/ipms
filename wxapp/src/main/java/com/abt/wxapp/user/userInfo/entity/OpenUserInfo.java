package com.abt.wxapp.user.userInfo.entity;


import com.abt.chkmodule.entity.UseChannel;
import com.abt.chkmodule.model.ChannelEnum;
import com.abt.common.config.ValidateGroup;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;


@Getter
@Setter
@Entity
@ToString
@Table(name = "open_user_info")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class OpenUserInfo implements UseChannel {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Size(max = 100, message = "用户名最多输入50字", groups = {ValidateGroup.Save.class})
    @NotNull(message = "用户名称不能为空", groups = {ValidateGroup.Save.class})
    @Column(name = "name", length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", length = 16)
    private ChannelEnum channel;

    @Column(name = "open_id", length = 100)
    private String openId;

    @Column(name = "phone", length = 20)
    private String phone;

    @Size(max = 100, message = "地址最多输入100字", groups = {ValidateGroup.Save.class})
    @Column(name = "address", length = 200)
    private String address;

}
