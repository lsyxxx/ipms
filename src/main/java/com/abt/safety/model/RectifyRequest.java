package com.abt.safety.model;

import com.abt.sys.model.SystemFile;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 */
@Getter
@Setter
public class RectifyRequest {
    private String id;
    private String rectifyRemark;
    private List<SystemFile> files;

}
