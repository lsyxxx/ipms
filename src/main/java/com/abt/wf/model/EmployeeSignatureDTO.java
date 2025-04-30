package com.abt.wf.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 员工基础信息+签名
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSignatureDTO {
    private String name;
    private String jobNumber;
    private String company;
    private String deptId;
    private String position;
    private String deptName;
    private String signatureId;
    private String fileName;
}
