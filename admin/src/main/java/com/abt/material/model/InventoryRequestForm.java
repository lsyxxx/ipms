package com.abt.material.model;

import com.abt.common.model.RequestForm;
import com.abt.material.entity.Inventory;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 */
@Getter
@Setter
public class InventoryRequestForm extends RequestForm {

    private List<String> materialTypeIds;
    private List<String> warehouseIds;

    private String materialId;
    private String warehouseId;

    private int year1;
    private int year2;
    private List<Integer> monthIn;

    private boolean showAlertOnly = false;

}
