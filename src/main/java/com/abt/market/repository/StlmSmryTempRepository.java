package com.abt.market.repository;

import com.abt.market.entity.StlmSmryTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StlmSmryTempRepository extends JpaRepository<StlmSmryTemp, String> {
    List<StlmSmryTemp> findByMid(String mid);

    List<StlmSmryTemp> findByMidOrderBySortNo(String mid);


    /**
     * 根据stlm_test_temp生成summry数据
     */
    @Modifying
    @Query(value = """
        insert into stlm_smry_temp (id, amt_, entrust_id, check_module_id, check_module_name, m_id, price_, sample_num, sort_no)
        SELECT NEWID(), sum(price_), entrust_id, check_module_id, check_module_name, :mainId, price_, count(sample_no), 
                       row_number() over(order by entrust_id, check_module_id) as sort_no
        FROM stlm_test_temp
        WHERE M_ID = :mainId
        group by entrust_id, check_module_id, check_module_name, price_
        """, nativeQuery = true)
    void createSummaryByTestItemTemp(String mainId);

}