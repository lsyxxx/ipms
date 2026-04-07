package com.abt.chkmodule.repository;

import com.abt.chkmodule.entity.Instrument;
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

}
