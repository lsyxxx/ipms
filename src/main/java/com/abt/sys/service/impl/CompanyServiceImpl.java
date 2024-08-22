package com.abt.sys.service.impl;

import com.abt.common.config.CommonSpecification;
import com.abt.common.entity.Company;
import com.abt.sys.model.entity.CustomerInfo;
import com.abt.sys.model.entity.SupplyInfo;
import com.abt.sys.repository.CustomerInfoRepository;
import com.abt.sys.repository.SupplyInfoRepository;
import com.abt.sys.service.CompanyService;
import com.abt.testing.model.CustomerRequestForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 *
 */
@Service
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    public static final String ABT_CODE = "ABT";
    public static final String ABT_NAME = "阿伯塔";
    public static final String ABT_SHORT_CODE = "A";
    public static final String GRD_CODE = "GRD";
    public static final String GRD_NAME = "吉瑞达";
    public static final String GRD_SHORT_CODE = "G";
    public static final String DC_CODE = "DC";
    public static final String DC_NAME = "道常";
    public static final String DC_SHORT_CODE = "D";

    private final CustomerInfoRepository customerInfoRepository;
    private final SupplyInfoRepository supplyInfoRepository;

    private final CustomerInfo abtCompany;
    private final CustomerInfo grdCompany;
    private final CustomerInfo dcCompany;

    public CompanyServiceImpl(CustomerInfoRepository customerInfoRepository, SupplyInfoRepository supplyInfoRepository, CustomerInfo abtCompany, CustomerInfo grdCompany, CustomerInfo dcCompany) {
        this.customerInfoRepository = customerInfoRepository;
        this.supplyInfoRepository = supplyInfoRepository;
        this.abtCompany = abtCompany;
        this.grdCompany = grdCompany;
        this.dcCompany = dcCompany;
    }


    @Override
    public Page<CustomerInfo> findAllClientPaged(CustomerRequestForm form) {
        Pageable page = PageRequest.of(form.jpaPage(), form.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        CustomerInfoSpecification spec = new CustomerInfoSpecification();
        Specification<CustomerInfo> criteria = Specification.where(spec.nameLike(form, "customerName"));
        return customerInfoRepository.findAll(criteria, page);
    }

    @Override
    public List<CustomerInfo> findAll() {
        return customerInfoRepository.findAllByIsAvtiveIs(CustomerInfoRepository.isAtiveTrue);
    }

    @Override
    public List<CustomerInfo> findYCompanyList() {
        return List.of(abtCompany, grdCompany);
    }

    @Override
    public List<String> findAllSupplier() {
        return supplyInfoRepository.findDistinctActiveSupplierNames();
    }

    @Override
    public SupplyInfo addSupplier(SupplyInfo supplyInfo) {
        return supplyInfoRepository.save(supplyInfo);
    }


    @Override
    public CustomerInfo addCustomer(CustomerInfo customer) {
        log.info("add customer : {}", customer);
        return customerInfoRepository.save(customer);
    }

    @Bean("ABT")
    @Order(1000)
    public Company ABT() {
        Company company = Company.of(ABT_CODE, ABT_NAME);
        company.setFullName(abtCompany.getCustomerName());
        company.setShortCode(ABT_SHORT_CODE);
        return company;
    }


    @Bean("GRD")
    @Order(1001)
    public Company GRD() {
        Company company = Company.of(GRD_CODE, GRD_NAME);
        company.setFullName(grdCompany.getCustomerName());
        company.setShortCode(GRD_SHORT_CODE);
        return company;
    }

    @Bean("DC")
    @Order(1002)
    public Company DC() {
        Company company = Company.of(DC_CODE, DC_NAME);
        company.setFullName(dcCompany.getCustomerName());
        company.setShortCode(DC_SHORT_CODE);
        return company;
    }


    class CustomerInfoSpecification extends CommonSpecification<CustomerRequestForm, CustomerInfo> {

    }
}
