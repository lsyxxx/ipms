package com.abt.common.service;

public interface CommonJpaAudit {

    String getCreateUserid();
    void setCreateUsername(String username);

    String getUpdateUserid();
    void setUpdateUsername(String username);

}
