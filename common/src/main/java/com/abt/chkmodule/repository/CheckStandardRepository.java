package com.abt.chkmodule.repository;

import com.abt.chkmodule.entity.CheckStandard;
import com.abt.chkmodule.model.StandardItemModuleUnitDTO;
import com.abt.chkmodule.model.StandardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CheckStandardRepository extends JpaRepository<CheckStandard, String> {


    @Query("""
    select c
    from CheckStandard c
    left join CheckItemStandardRel rel on c.id = rel.standardId
    where rel.checkItemId in :checkItemIds
""")
    List<CheckStandard> findByCheckItemIds(List<String> checkItemIds);


    @Query("""
            SELECT c
            FROM CheckStandard c
            JOIN CheckItemStandardRel rel ON c.id = rel.standardId
            WHERE rel.checkItemId = :itemId
            """)
    List<CheckStandard> findStandardsByItemId(String itemId);

    @Query("""
            SELECT c FROM CheckStandard c
            WHERE (:query IS NULL OR :query = '' OR
                   c.code LIKE CONCAT('%', :query, '%') OR
                   c.name LIKE CONCAT('%', :query, '%'))
            AND (:status IS NULL OR c.status = :status)
            AND (:hasLevels = false OR c.level IN :levels)
            """)
    Page<CheckStandard> findStandardPage(String query,
                                         StandardStatus status,
                                         boolean hasLevels,
                                         List<String> levels,
                                         Pageable pageable);

    @Query("SELECT DISTINCT c.level FROM CheckStandard c WHERE c.level IS NOT NULL AND c.level != ''")
    List<String> findDistinctLevels();

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, String id);


    @Query("""
            SELECT new com.abt.chkmodule.model.StandardItemModuleUnitDTO(
                rel.chapterNo,
                ci.id,
                ci.code,
                ci.name,
                ci.description,
                ci.isCma,
                ci.isCnas,
                ci.restrict,
                cm.id,
                cm.name,
                cu.id,
                cu.name
            )
            FROM CheckStandard std
            LEFT JOIN CheckItemStandardRel rel ON std.id = rel.standardId
            LEFT JOIN CheckItem ci ON rel.checkItemId = ci.id
            LEFT JOIN CheckModule cm ON ci.checkModuleRef.checkModuleId = cm.id
            LEFT JOIN CheckUnit cu ON cm.checkUnitId = cu.id
            WHERE std.id = :standardId
            """)
    List<StandardItemModuleUnitDTO> findUnitItemsModulesByStandardId(String standardId);
}