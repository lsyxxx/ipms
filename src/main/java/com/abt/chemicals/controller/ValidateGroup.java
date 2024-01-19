package com.abt.chemicals.controller;

import jakarta.validation.GroupSequence;

/**
 *
 */
public class ValidateGroup {
    public interface Insert{}
    public interface Update{};
    public interface Delete{};

    @GroupSequence({Insert.class, Update.class, Delete.class})
    public interface All{};
    @GroupSequence({Insert.class, Update.class})
    public interface Save{};

}
