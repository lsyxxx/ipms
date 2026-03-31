package com.abt.chkmodule.repository;

import com.abt.chkmodule.entity.CheckModule;
import com.abt.chkmodule.entity.CheckUnit;
import com.abt.chkmodule.model.ChannelEnum;
import com.abt.common.repository.EnabledRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CheckUnitRepository extends JpaRepository<CheckUnit, String>, EnabledRepository<CheckModule, String> {

    @Query("""
    select c from CheckUnit c
    where (:channel is null or c.useChannel = :channel)
    and (:enabled is null or c.enabled = :enabled)
""")
    List<CheckUnit> findList(ChannelEnum channel, Boolean enabled);
}