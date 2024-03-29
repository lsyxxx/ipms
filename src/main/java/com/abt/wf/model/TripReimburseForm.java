package com.abt.wf.model;

import com.abt.wf.entity.TripReimburse;
import lombok.Data;

import java.util.List;

/**
 * 差旅报销form
 */
@Data
public class TripReimburseForm {

    private String comment;
    private String decision;

    /**
     * 报销明细
     */
    private List<TripReimburseForm> items;

    //TODO: 比继承好吗
    private TripReimburse entity;

}
