package com.abt.common.service;


public interface UserJpaAudit {

    String getCreateUserid();
    String getCreateUsername();
    String getUpdateUserid();
    String getUpdateUsername();
    void setUpdateUserid(String updateUserid);
    void setCreateUserid(String createUserid);
    void setCreateUsername(String createUsername);
    void setUpdateUsername(String updateUsername);
}
