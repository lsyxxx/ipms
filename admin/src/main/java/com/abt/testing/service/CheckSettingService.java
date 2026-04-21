package com.abt.testing.service;

import com.abt.chkmodule.model.ChannelEnum;
import com.abt.testing.model.CheckModuleSyncResult;

public interface CheckSettingService {
    /**
     * 同步checkunit
     * 1. 名称一样的不同步
     */
    void synchronizeTcheckunitDB(ChannelEnum channel);

    /**
     * 将旧数据库T_checkModule的数据同步到新的check_module数据。复制后的check_module数据是未发布的
     * 批处理，100条数据一次。
     * 根据老的fid查询新的checkunit中code的分类。要保证分类已先同步
     * 规则：
     * 1. 相同channel和checkUnitId的情况下，存在相同名称则不同步(T_checkmodule.Fname=check_module.name)
     *
     * @return 返回无法同步的t_checkmodule数据，并返回错误信息
     */
    CheckModuleSyncResult synchronizeTcheckmoduleDB(ChannelEnum channel);
}
