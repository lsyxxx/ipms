package com.abt.market.service;

import com.abt.market.entity.SettlementMain;
import com.abt.market.entity.SettlementRelation;
import com.abt.market.model.SettlementMainListDTO;
import com.abt.market.model.SettlementRelationType;
import com.abt.market.model.SettlementRequestForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
}
