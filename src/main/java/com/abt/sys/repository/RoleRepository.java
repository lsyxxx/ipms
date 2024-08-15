package com.abt.sys.repository;

import com.abt.sys.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.HashSet;
import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Object> {

    @Query("select ro " +
            "from Role ro " +
            "left join Relevance re on ro.id = re.secondId " +
            "where re.firstId = :userid " +
            "and re.key = 'UserRole'")
    HashSet<Role> getRolesByUserid(String userid);
}