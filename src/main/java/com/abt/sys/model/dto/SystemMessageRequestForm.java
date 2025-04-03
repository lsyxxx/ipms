package com.abt.sys.model.dto;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 */
@Getter
@Setter
public class SystemMessageRequestForm extends RequestForm {
    private List<String> typeIds;
    private String toId;
    private Integer toStatus;

    public List<String> buildTypeIds() {
        if (this.typeIds == null || this.typeIds.isEmpty()) {
            this.typeIds = List.of("all");
        }
        return this.typeIds;
    }

}
