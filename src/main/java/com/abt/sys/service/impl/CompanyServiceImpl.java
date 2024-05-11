package com.abt.sys.service.impl;

import com.abt.common.config.CommonSpecification;
import com.abt.common.entity.Company;
import com.abt.sys.model.entity.CustomerInfo;
import com.abt.sys.model.entity.SupplyInfo;
import com.abt.sys.repository.CustomerInfoRepository;
import com.abt.sys.repository.SupplyInfoRepository;
import com.abt.sys.service.CompanyService;
import com.abt.testing.model.CustomerRequestForm;
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
public class CompanyServiceImpl implements CompanyService {

    public static final String ABT_CODE = "ABT";
    public static final String ABT_NAME = "阿伯塔";
    public static final String GRD_CODE = "GRD";
    public static final String GRD_NAME = "吉瑞达";
    public static final String DC_CODE = "DC";
    public static final String DC_NAME = "道常";

    @Override
    public List<Company> userCompany() {
        return List.of(ABT(), GRD(), DC());
    }


    public static Company ABT() {
        return Company.of(ABT_CODE, ABT_NAME);
    }

    public static Company GRD() {
        return Company.of(GRD_CODE, GRD_NAME);
    }

    public static Company DC() {
        return Company.of(DC_CODE, DC_NAME);
    }


    private final CustomerInfoRepository customerInfoRepository;
    private final SupplyInfoRepository supplyInfoRepository;

    private final CustomerInfo abtCompany;
    private final CustomerInfo grdCompany;

    public CompanyServiceImpl(CustomerInfoRepository customerInfoRepository, SupplyInfoRepository supplyInfoRepository, CustomerInfo abtCompany, CustomerInfo grdCompany) {
        this.customerInfoRepository = customerInfoRepository;
        this.supplyInfoRepository = supplyInfoRepository;
        this.abtCompany = abtCompany;
        this.grdCompany = grdCompany;
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
        return customerInfoRepository.save(customer);
    }


    class CustomerInfoSpecification extends CommonSpecification<CustomerRequestForm, CustomerInfo> {

    }
}
