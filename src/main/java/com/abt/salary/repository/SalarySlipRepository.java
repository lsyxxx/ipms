package com.abt.salary.repository;

import com.abt.salary.entity.SalarySlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SalarySlipRepository extends JpaRepository<SalarySlip, String> {

    /**
     * 根据mainId 查询存在问题的工资条数据(ErrorIsNotEmpty)
     * @param mainId sl_main 主键
     */
    @Query("select s from SalarySlip  s where s.mainId = :mainId and s.error is not null and s.error <> ''")
    List<SalarySlip> findByMainIdAndErrorNotEmpty(String mainId);
    List<SalarySlip> findByMainId(String mainId);
    List<SalarySlip> findByMainIdAndIsSend(String mainId, boolean isSend);

    int countByMainIdAndIsSend(String mainId, boolean isSend);
    int countByMainIdAndIsCheck(String mainId, boolean isCheck);
    int countByMainIdAndIsRead(String mainId, boolean isRead);
}