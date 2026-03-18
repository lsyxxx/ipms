package com.abt.sys.controller;

import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.entity.CustomerInfo;
import com.abt.sys.model.entity.SupplyInfo;
import com.abt.sys.service.CompanyService;
import com.abt.testing.model.CustomerRequestForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

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
        final List<CustomerInfo> all = companyService.findAll();
        return R.success(all);
    }

    /**
     * 所有供应商
     */
    @GetMapping("/s/all")
    public R<List<String>> getAllSupplier() {
        final List<String> allSupplier = companyService.findAllSupplier();
        return R.success(allSupplier);
    }

    @PostMapping("/s/add")
    public R<SupplyInfo> addSupplier(@RequestBody SupplyInfo supplyForm) {
        final SupplyInfo supplyInfo = companyService.addSupplier(supplyForm);
        return R.success(supplyInfo, "添加成功");
    }

    @PostMapping("/j/add")
    public R<CustomerInfo> addCutomer(@RequestBody CustomerInfo customerForm) {
        customerForm.setCreateUserId(TokenUtil.getUseridFromAuthToken());
        final CustomerInfo customerInfo = companyService.addCustomer(customerForm);
        return R.success(customerInfo, "添加成功");
    }



}
