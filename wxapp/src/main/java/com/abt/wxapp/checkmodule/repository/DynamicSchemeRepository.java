package com.abt.wxapp.checkmodule.repository;

import com.abt.wxapp.checkmodule.entity.DynamicScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DynamicSchemeRepository extends JpaRepository<DynamicScheme, Long> {

    /**
     * 同一检测项目下：自增主键 id 最大的一条（即最新插入的一版）
     */
    Optional<DynamicScheme> findFirstByCheckModuleIdOrderByIdDesc(String checkModuleId);

    /**
     * 每个检测项目（cm_id）仅一条：该记录下 id 最大，即最新一版
     */
    @Query("""
            select d from DynamicScheme d
            where d.id = (
                select max(d2.id) from DynamicScheme d2 where d2.checkModuleId = d.checkModuleId
            )
            """)
    List<DynamicScheme> findAllLatestPerCheckModule();
}
