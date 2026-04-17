package com.abt.chkmodule.model;

import com.abt.chkmodule.entity.CheckItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 检测项目简单显示
 */
@Getter
@Setter
@NoArgsConstructor
public class SimpleCheckModule {
    private String id;
    private String name;
    private String code;

    private String checkUnitId;
    private String checkUnitName;

    private List<CheckItem> checkItems;

    public SimpleCheckModule(String id, String name, String code, String checkUnitId) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.checkUnitId = checkUnitId;
    }

    public SimpleCheckModule(String id, String name, String code, String checkUnitId, String checkUnitName) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.checkUnitId = checkUnitId;
        this.checkUnitName = checkUnitName;
    }

}
