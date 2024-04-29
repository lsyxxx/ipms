package com.abt.sys.service;

import java.util.List;
import com.abt.common.entity.Company;
import com.abt.sys.model.entity.CustomerInfo;
import com.abt.sys.model.entity.SupplyInfo;
import com.abt.testing.model.CustomerRequestForm;
import org.springframework.data.domain.Page;

public interface CompanyService {

    List<Company> userCompany();


    /**
     * 查询所有客户
     * @param form
     * @return
     */
    Page<CustomerInfo> findAllClientPaged(CustomerRequestForm form);

    List<CustomerInfo> findAll();

    /**
     * 查询乙方公司
     */
    List<CustomerInfo> findYCompanyList();

    /**
     * 查询所有供应商
     */
    List<SupplyInfo> findAllSupplier();
}
