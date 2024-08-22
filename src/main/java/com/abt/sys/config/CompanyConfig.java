package com.abt.sys.config;

import com.abt.common.entity.Company;
import com.abt.sys.model.entity.CustomerInfo;
import com.abt.sys.repository.CustomerInfoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Component
public class CompanyConfig {

    private final CustomerInfoRepository customerInfoRepository;

    @Value("${abt.company.abt.id}")
    private String abtCompanyId;
    @Value("${abt.company.grd.id}")
    private String grdCompanyId;

    @Value("${abt.company.dc.id}")
    private String dcCompanyId;

    private Map<String, CustomerInfo> companyMap = new HashMap<>();

    public CompanyConfig(CustomerInfoRepository customerInfoRepository) {
        this.customerInfoRepository = customerInfoRepository;
    }

    /**
     * 阿伯塔公司信息
     */
    @Bean
    @Order(1)
    public CustomerInfo abtCompany() {
        final CustomerInfo abt = customerInfoRepository.findById(abtCompanyId).orElse(new CustomerInfo().setCustomerName("西安阿伯塔资环分析测试技术有限公司"));
        companyMap.put(abtCompanyId, abt);
        return abt;
    }

    /**
     * 吉瑞达公司信息
     */
    @Bean
    @Order(1)
    public CustomerInfo grdCompany() {
        final CustomerInfo grd = customerInfoRepository.findById(grdCompanyId).orElse(new CustomerInfo().setCustomerName("西安吉瑞达地质科技有限公司"));
        companyMap.put(grdCompanyId, grd);
        return grd;
    }

    @Bean
    @Order(2)
    public CustomerInfo dcCompany() {
        final CustomerInfo grd = customerInfoRepository.findById(dcCompanyId).orElse(new CustomerInfo().setCustomerName("陕西道常能源技术有限公司"));
        companyMap.put(grdCompanyId, grd);
        return grd;
    }

    @Bean
    @Order(100)
    public Map<String, CustomerInfo> companyMap() {
        return this.companyMap;
    }


}
