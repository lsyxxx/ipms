package com.abt.chkmodule.repository;

import com.abt.chkmodule.entity.Instrument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InstrumentRepository extends JpaRepository<Instrument, String> {

    @Query("""
            SELECT i FROM Instrument i
            JOIN CheckModuleInstrumentRel rel ON i.id = rel.instrumentId
            WHERE rel.checkModuleRef.checkModuleId = :checkModuleId
            """)
    List<Instrument> findByCheckModule(@Param("checkModuleId") String checkModuleId);

    @Query("""
            SELECT i FROM Instrument i
            WHERE (:query IS NULL OR :query = '' OR
                   i.code LIKE CONCAT('%', :query, '%') OR
                   i.name LIKE CONCAT('%', :query, '%') OR
                   i.type LIKE CONCAT('%', :query, '%') OR
                   i.boxNo LIKE CONCAT('%', :query, '%') OR
                   i.specification LIKE CONCAT('%', :query, '%') OR
                   i.useDept LIKE CONCAT('%', :query, '%') OR
                   i.username LIKE CONCAT('%', :query, '%') OR
                   i.manufacturer LIKE CONCAT('%', :query, '%') OR
                   i.supplier LIKE CONCAT('%', :query, '%') OR
                   i.setupAddress LIKE CONCAT('%', :query, '%'))
            AND (:hasTypes = false OR i.type IN :types)
            AND (:status IS NULL OR :status = '' OR i.status = :status)
            AND (:hasUseDepts = false OR i.useDept IN :useDepts)
            """)
    Page<Instrument> findInstrumentPage(String query,
                                        boolean hasTypes,
                                        List<String> types,
                                        String status,
                                        boolean hasUseDepts,
                                        List<String> useDepts,
                                        Pageable pageable);


    @Query("SELECT MAX(i.sortNo) FROM Instrument i WHERE i.code LIKE CONCAT(:prefix, '%')")
    Integer findMaxSortNoByPrefix(String prefix);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, String id);

}
