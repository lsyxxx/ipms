package com.abt.testing.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 预登记合同导入错误，行号与 Excel 实际行号一致。 */
@Getter
@AllArgsConstructor
public class PreAgreementImportError {
    private final int rowNumber;
    private final String field;
    private final String value;
    private final String message;
}
