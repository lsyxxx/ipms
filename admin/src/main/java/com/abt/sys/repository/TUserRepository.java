package com.abt.sys.repository;

import com.abt.sys.model.entity.TUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface TUserRepository extends JpaRepository<TUser, String> {

    /** 使用现有 UserRole/RoleModule 关联校验打卡上传菜单权限，兼容 SQL Server 2014。 */
    @Query(value = "SELECT COUNT(*) FROM [User] u WHERE u.Id = :userId AND u.Status = 0 " +
            "AND (u.Account = 'System' OR EXISTS (SELECT 1 FROM Relevance ur " +
            "JOIN Relevance rm ON rm.FirstId = ur.SecondId AND rm.[Key] = 'RoleModule' " +
            "JOIN Module m ON m.Id = rm.SecondId " +
            "WHERE ur.FirstId = u.Id AND ur.[Key] = 'UserRole' " +
            "AND m.Url = '/dakalog/setting' AND m.Status = 0))", nativeQuery = true)
    long countApprovalReminderPermission(String userId);

    TUser findByEmpnum(String empnum);

    List<TUser> findAllByEmpnumIn(Collection<String> empnums);
}
