package com.abt.testing.service;

import com.abt.testing.model.PreAgreementImportResult;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.OutputStream;

/** 预登记合同 Excel 导入及模板服务。 */
public interface PreAgreementImportService {
    PreAgreementImportResult save(MultipartFile file);
    void findTemplate(OutputStream output) throws IOException;
}
