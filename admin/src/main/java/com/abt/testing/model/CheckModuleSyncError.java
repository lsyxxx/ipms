package com.abt.testing.model;

import com.abt.testing.entity.TCheckmodule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckModuleSyncError {

    private String fname;
    private String fid;
    private String checkUnitId;
    private String checkUnitName;

    private String error;

    public CheckModuleSyncError(String fname, String fid, String error) {
        this.fname = fname;
        this.fid = fid;
        this.error = error;
    }

    public boolean hasError() {
        return StringUtils.isNotBlank(error);
    }

}
