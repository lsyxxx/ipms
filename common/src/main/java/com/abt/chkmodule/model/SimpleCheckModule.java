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

    private String checkUnitId;

    private List<CheckItem> checkItems;

    public SimpleCheckModule(String id, String name, String code, String checkUnitId) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.checkUnitId = checkUnitId;
    }

}
