package com.abt.chkmodule.model;

import com.abt.chkmodule.entity.CheckItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 检测项目简单显示
 */
@Getter
@Setter
public class SimpleCheckModule {
    private String id;
    private String name;
    private String code;

    private List<CheckItem> checkItems;
}
