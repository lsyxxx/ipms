package com.abt.market.service;

import com.abt.market.entity.SaleAgreement;
import com.abt.market.model.AgreementInvoiceSummary;
import com.abt.market.model.SaleAgreementRequestForm;
import org.springframework.data.domain.Page;

import java.io.OutputStream;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface SaleAgreementService {
    Page<SaleAgreement> findByQuery(SaleAgreementRequestForm requestForm);

    Page<SaleAgreement> findPaged(SaleAgreementRequestForm requestForm);

    void delete(String id);

    List<String> findAllPartyA();

    List<SaleAgreement> findSaleAgreementCreatedByCurrentWeek();

    List<SaleAgreement> findSaleAgreementCreatedByCurrentMonth();

    List<SaleAgreement> findSaleAgreementCreatedByCurrentYear();

    long countAllSaleAgreement();

    void saveSaleAgreement(SaleAgreement saleAgreement);

    SaleAgreement LoadSaleAgreement(String id);

    //市场看板数据
    Map<String, Object> marketBoardData(int currentYear);

    void exportSaleAgreementList(List<SaleAgreement> list, OutputStream outputStream) throws Exception;

//    /**
//     * 统计某年月某合同的开票金额
//     * @param yearMonth 指定年月
//     * @param id 合同id
//     */
//    List<AgreementInvoiceSummary> summaryByMonth(YearMonth yearMonth, String id);
}
