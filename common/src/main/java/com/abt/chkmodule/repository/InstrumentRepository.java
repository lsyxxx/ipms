package com.abt.chkmodule.repository;

import com.abt.chkmodule.entity.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InstrumentRepository extends JpaRepository<Instrument, String> {



    @Query("""
    select i
    from Instrument i
    left join CheckModuleInstrumentRel rel on i.id = rel.instrumentId
    where rel.checkModuleRef.checkModuleId = :checkModuleId
""")
    List<Instrument> findByCheckModule(String checkModuleId);

}