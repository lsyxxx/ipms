package com.abt.testing.model;

import com.abt.testing.entity.TCheckmodule;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 同步检测项目结果
 */
@Getter
@Setter
public class CheckModuleSyncResult {
    private List<CheckModuleSyncError> error;

    /**
     * 已同步数量
     */
    private int syncCount;

    /**
     * t_checkmodule总数
     */
    private long allCount;

}
