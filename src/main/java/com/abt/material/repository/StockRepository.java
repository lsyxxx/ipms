package com.abt.material.repository;

import com.abt.material.entity.Stock;
import com.abt.material.model.IMaterialDetailDTO;
import com.abt.material.model.MonthlyStockStatsDTO;
import com.abt.material.model.StockQuantitySummary;
import com.abt.material.model.StockType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StockRepository extends JpaRepository<Stock, String> {

    @Query("select st from Stock st " +
            "left join fetch StockOrder so on st.orderId = so.id " +
            "where (:stockType is null or so.stockType = :stockType) " +
            "and (:query is null or :query = '' or st.orderId like %:query% or st.materialName like %:query%) " +
            "and (:materialTypeName is null or :materialTypeName = '' or st.materialTypeName like %:materialTypeName%) " +
            "and ('all' in :warehouseIds or so.warehouseId in :warehouseIds) " +
            "and (:startDate is null or so.orderDate >= :startDate) " +
            "and (:endDate is null or so.orderDate <= :endDate) " +
            "order by so.orderDate desc, so.stockType asc, st.materialName asc")
    Page<Stock> findByQueryPageable(String query, Integer stockType, List<String> warehouseIds, String materialTypeName, LocalDate startDate, LocalDate endDate, Pageable pageable);

    void deleteByOrderId(@NotNull @Size(max = 64) String orderId);


    @Query("""
                   select new com.abt.material.model.StockQuantitySummary(st.materialName, st.specification, st.unit, sum(st.num), so.stockType, so.warehouseId, so.warehouseName)
                   from Stock st
                   left join fetch StockOrder so on st.orderId = so.id
                   where so.stockType = :stockType
                   and st.materialTypeName like '%礼品类%'
                   and (:startDate is null or so.orderDate >= :startDate)
                   and (:endDate is null or so.orderDate <= :endDate)
                   group by st.materialName, st.specification, st.unit, so.stockType, so.warehouseId, so.warehouseName
            """)
    List<StockQuantitySummary> summaryGiftQuantity(Integer stockType, LocalDate startDate, LocalDate endDate);

    /**
     * 获取指定物料在指定年份的月统计数据
     *
     * @param year           年份
     * @param monthIn        包含的月份
     * @param stockTypeValue 出入库类型值（1:入库, 2:出库）
     * @param typeIds        物料类别id
     */
    @Query("""
                select new com.abt.material.model.MonthlyStockStatsDTO(
                    year(so.orderDate),
                    month(so.orderDate),
                    st.materialTypeId,
                    st.materialTypeName,
                    st.materialId,
                    st.materialName,
                    st.specification,
                    md.price,
                    st.unit,
                    so.stockType,
                    sum(st.num),
                    sum(st.totalPrice),
                    count(st.id),
                    so.warehouseId,
                    so.warehouseName
                )
                from Stock st
                join StockOrder so on st.orderId = so.id
                left join MaterialDetail md on st.materialId = md.id
                where year(so.orderDate) = :year
                and (month(so.orderDate) in :monthIn)
                and ('all' in :typeIds or st.materialTypeId in :typeIds)
                and (:stockTypeValue is null or so.stockType = :stockTypeValue)
                group by year(so.orderDate), month(so.orderDate),
                         st.materialTypeId, st.materialTypeName,
                         st.materialId, st.materialName,
                         st.specification, st.unit, so.stockType,
                         so.warehouseId, so.warehouseName,
                         md.price
                order by month(so.orderDate), st.materialName
            """)
    List<MonthlyStockStatsDTO> findMonthlyDataBy(@Param("year") int year, List<String> typeIds,
                                                 @Param("monthIn") List<Integer> monthIn,
                                                 @Param("stockTypeValue") Integer stockTypeValue);


    /**
     * 获取指定礼品类在指定年份的月统计数据
     *
     * @param year           年份
     * @param monthIn        包含的月份
     * @param stockTypeValue 出入库类型值（1:入库, 2:出库）
     */
    @Query("""
                select new com.abt.material.model.MonthlyStockStatsDTO(
                    year(so.orderDate),
                    month(so.orderDate),
                    st.materialTypeId,
                    st.materialTypeName,
                    st.materialId,
                    st.materialName,
                    st.specification,
                    md.price,
                    st.unit,
                    so.stockType,
                    sum(st.num),
                    sum(st.totalPrice),
                    count(st.id)
                )
                from Stock st
                join StockOrder so on st.orderId = so.id
                left join MaterialDetail md on st.materialId = md.id
                where year(so.orderDate) = :year
                and month(so.orderDate) in :monthIn
                and st.materialTypeName like '礼品类%'
                and so.stockType = :stockTypeValue
                group by year(so.orderDate), month(so.orderDate),
                         st.materialTypeId, st.materialTypeName,
                         st.materialId, st.materialName,
                         st.specification, st.unit, so.stockType,
                         md.price
                order by st.materialTypeName,  md.price desc, month(so.orderDate) asc
            """)
    List<MonthlyStockStatsDTO> findGiftMonthlyDataBy(@Param("year") int year, @Param("monthIn") List<Integer> monthIn, @NotNull @Param("stockTypeValue") Integer stockTypeValue);

}