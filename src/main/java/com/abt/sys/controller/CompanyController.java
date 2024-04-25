package com.abt.sys.controller;

import com.abt.common.model.R;
import com.abt.sys.model.entity.CustomerInfo;
import com.abt.sys.service.CompanyService;
import com.abt.testing.model.CustomerRequestForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/com")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * 查询乙方公司
     */
    @GetMapping("/y/all")
    public R<List<CustomerInfo>> getABTCompany() {
        final List<CustomerInfo> yCompanyList = companyService.findYCompanyList();
        return R.success(yCompanyList);
    }

    /**
     * 查询甲方公司(客户)
     */
    @GetMapping("/j/all")
    public R<List<CustomerInfo>> getAllJCompany(CustomerRequestForm form) {
        final Page<CustomerInfo> all = companyService.findAllClientPaged(form);
        return R.success(all.getContent(), (int)all.getTotalElements());
    }
}
