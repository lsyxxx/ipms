package com.abt.market.service;

import com.abt.market.entity.*;
import com.abt.market.model.*;
import com.abt.sys.model.entity.CustomerInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 检测项目结算
 */
public interface SettlementService {
    void save(SettlementMain main);

    /**
     * 仅获取settlementMain实体
     */
    SettlementMain findSettlementMainOnly(String id);

    /**
     * 获取实体类及所有关联数据
     * @param id 结算单id
     */
    SettlementMain findSettlementMainWithAllItems(String id);


    /**
     * 删除结算单及所有关联的
     * @param id 结算单id
     */
    void delete(String id);

    /**
     * 根据关键字查询，只返回settlementMain的分页结果，不包含关联数据
     */
    Page<SettlementMainListDTO> findMainOnlyByQuery(SettlementRequestForm requestForm, Pageable pageable);

    /**
     * 生成结算单据
     * @param settlementMain 结算数据
     * @param outputStream 输出流
     */
    void createSettlementExcel(SettlementMain settlementMain, java.io.OutputStream outputStream);

    /**
     * 保存关联信息
     * @param srs 关联信息对象列表
     * @param mid 结算单id
     * @return int 返回保存成功的数量，
     */
    int saveRef(List<SettlementRelation> srs, String mid);

    /**
     * 删除结算单关联的一类单据
     * @param mid 结算单id
     * @param bizType 业务类型
     */
    void deleteRefByBizType(String mid, SettlementRelationType bizType);

    /**
     * 根据结算id和关联id删除
     * @param mid 结算id
     * @param rid 关联id
     */
    void deleteRefBy(String mid, String rid);

    /**
     * 作废结算单
     * 1. 暂存的结算单：直接作废，仅修改saveType
     * 2. 已完成的结算单：1. 查看是否开发票，若未开发票，直接作废。2. 若已开发票，必须有相同金额的负数发票才可以
     * 3. 已作废：不变
     */
    void invalid(SettlementMain main);

    /**
     * 获取所有为作废的结算单的客户
     */
    List<CustomerInfo> getClients();

    void updateRelations(RelationRequest relationRequest);

    /**
     * 更新状态
     */
    void updateSaveType(SaveType saveType, String id);

    /**
     * 按检测编号导入
     *
     * @param list 导入excel数据
     */
    String importBySamples(List<ImportSample> list);

    /**
     * 根据tempMid删除stlm_test_temp
     * @param tempMid stlm_test_temp的mid
     */
    void deleteTempByTempMid(String tempMid);

    /**
     * 查询
     * @param requestForm 查询form
     */
    Page<StlmTestTemp> findTestTempByQuery(TestTempRequestForm requestForm);

    /**
     * 使用testTemp临时数据生成summaryTableData
     */
    List<SettlementSummary> createSummaryTableByTestTemp(String tempMid);

    /**
     * 导出委托单样品到Excel
     * @param items 样品检测项目列表
     * @param outputStream 输出流
     */
    void exportTestTempToExcel(List<com.abt.testing.entity.SampleRegistCheckModeuleItem> items, java.io.OutputStream outputStream) throws Exception;

    /**
     * 保存临时导入的数据
     * @return String
     */
    String importBySummaryData(MultipartFile file) throws Exception;

    List<StlmSmryTemp> getTempSummaryData(String tempId);

    /**
     * 逻辑删除
     * @param id id
     */
    void logicDelete(String id);
}
