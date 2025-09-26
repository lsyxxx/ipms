package com.abt.wf.model;

import com.abt.wf.entity.SubcontractTestingSample;
import com.abt.wf.entity.SubcontractTestingSettlementDetail;
import lombok.Getter;
import lombok.Setter;

/**
 * 重复样品信息
 */
@Getter
@Setter
public class DuplicatedSample {
    private String id;
    private String mid;
    private String oldSampleNo;
    private String newSampleNo;
    private String checkModuleId;
    private String checkModuleName;
    private String error;


    public static DuplicatedSample from(SubcontractTestingSample sample) {
        DuplicatedSample duplicatedSample = new DuplicatedSample();
        duplicatedSample.setId(sample.getId());
        duplicatedSample.setMid(sample.getMid());
        duplicatedSample.setOldSampleNo(sample.getOldSampleNo());
        duplicatedSample.setNewSampleNo(sample.getNewSampleNo());
        duplicatedSample.setCheckModuleId(sample.getCheckModuleId());
        duplicatedSample.setCheckModuleName(sample.getCheckModuleName());
        return duplicatedSample;
    }

    public static DuplicatedSample from(SubcontractTestingSettlementDetail sample) {
        DuplicatedSample duplicatedSample = new DuplicatedSample();
        duplicatedSample.setId(sample.getId());
        duplicatedSample.setOldSampleNo(sample.getSampleNo());
        duplicatedSample.setCheckModuleId(sample.getCheckModuleId());
        duplicatedSample.setCheckModuleName(sample.getCheckModuleName());
        return duplicatedSample;
    }



}
