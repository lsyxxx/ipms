package com.abt.sys.service.impl;

import com.abt.common.entity.Company;
import com.abt.sys.service.CompanyService;
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
}
