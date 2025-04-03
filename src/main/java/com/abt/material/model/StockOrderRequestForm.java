package com.abt.material.model;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 */
@Getter
@Setter
public class StockOrderRequestForm extends RequestForm {

    private Integer stockType;

    private List<String> warehouseIds;

    public void buildForm() {
        if (this.warehouseIds == null || this.warehouseIds.isEmpty()) {
            this.warehouseIds = List.of("all");
        }
    }
}
