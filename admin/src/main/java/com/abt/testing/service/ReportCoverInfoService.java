package com.abt.testing.service;

import com.abt.testing.model.ReportCoverInfoRequest;
import com.abt.testing.model.ReportCoverInfoRequestForm;
import com.abt.testing.model.ReportCoverInfoVO;
import org.springframework.data.domain.Page;

import java.util.List;

/** 报告封面信息维护业务接口。 */
public interface ReportCoverInfoService {
    ReportCoverInfoVO findById(String id);
    Page<ReportCoverInfoVO> findPage(ReportCoverInfoRequestForm form);
    List<ReportCoverInfoVO> findList(ReportCoverInfoRequestForm form);
    ReportCoverInfoVO save(ReportCoverInfoRequest request);
    ReportCoverInfoVO update(ReportCoverInfoRequest request);
    void delete(String id);
}
