package com.abt.salary.repository;

import com.abt.salary.entity.SalaryCell;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface SalaryCellRepository extends JpaRepository<SalaryCell, String> {

    List<SalaryCell> findByJobNumberAndYearMonthOrderBySlipIdAscColumnIndexAsc(String jobNumber, String yearMonth);

    List<SalaryCell> findBySlipIdOrderByColumnIndex(String slipId);
    List<SalaryCell> findBySlipIdAndValueIsNotNullOrderByColumnIndexAsc(String slipId);

    @Modifying
    @Query("delete from SalaryCell c where c.mid = :mid")
    int deleteAllByMid(String mid);

    List<SalaryCell> findByMid(@Size(max = 255) String mid, Sort sort);

    List<SalaryCell> findByMidAndJobNumber(String mid, String jobNumber);

    @Query("""
        select sl from SalaryCell sl 
        left join EmployeeInfo e on sl.jobNumber = e.jobNumber 
        left join Org o on e.dept = o.id 
        where sl.mid = :mid
        and o.id in :depts
        order by sl.jobNumber, sl.rowIndex, sl.columnIndex
""")
    List<SalaryCell> findByMidAndDepts(String mid, List<String> depts);

    @Query("""
        select sl from SalaryCell sl 
        where sl.slipId in :sids 
        order by sl.jobNumber, sl.rowIndex, sl.columnIndex
""")
    List<SalaryCell> findBySidList(List<String> sids);

    @Query("""
       select sc from SalaryCell sc where (:yearMonth is null or :yearMonth = '' or sc.yearMonth = :yearMonth) order by sc.yearMonth desc, sc.jobNumber asc, sc.rowIndex asc, sc.columnIndex asc
""")
    List<SalaryCell> findByYearMonth(String yearMonth);

    List<SalaryCell> findByMidIn(Collection<String> mids);

    List<SalaryCell> findByMid(@Size(max = 255) String mid);

    @Transactional
    @Query("update SalaryCell s set s.value = :value where s.id = :id")
    @Modifying
    Integer updateValueById(@Size(max = 255) String value, String id);

    List<SalaryCell> findByColumnIndexAndMidOrderByRowIndex(@NotNull Integer columnIndex, @Size(max = 255) String mid);

    SalaryCell findBySlipIdAndColumnIndex(String slipId, Integer columnIndex);
}