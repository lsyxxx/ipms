package com.abt.salary.service;


import com.abt.salary.entity.SalaryMain;
import com.abt.salary.model.SalaryPreview;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface SalaryService {
    SalaryMain createSalaryMain(MultipartFile file, String yearMonth, String group);

    /**
     * 抽取excel数据
     */
    SalaryPreview extractAndValidate(InputStream fis, String mainId);
}
