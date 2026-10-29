package com.abt.testing.model;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

/** 整批导入结果；存在错误时保存数量为零。 */
@Getter
@Setter
public class PreAgreementImportResult {
    private int totalCount;
    private int savedCount;
    private final List<PreAgreementImportError> errors = new ArrayList<>();
}
