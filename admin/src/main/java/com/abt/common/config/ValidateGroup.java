package com.abt.common.config;

import jakarta.validation.GroupSequence;

/**
 *
 */
public class ValidateGroup {
    public interface Insert{}
    public interface Update{};
    public interface Delete{};

    public interface Preview{};
    public interface Apply{};
    //草稿
    public interface Temp{};

    @GroupSequence({Insert.class, Update.class, Delete.class, Preview.class})
    public interface All{};
    @GroupSequence({Insert.class, Update.class})
    public interface Save{};

}
