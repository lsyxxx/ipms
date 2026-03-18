package com.abt.wf.repository;

import com.abt.wf.entity.UserSignature;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface UserSignatureRepository extends JpaRepository<UserSignature, String> {

    UserSignature findByJobNumber(String jobNumber);
    UserSignature findByUserId(String userId);

    List<UserSignature> findByJobNumberIn(Collection<String> jobNumbers);

    @Query("""
        select new com.abt.wf.entity.UserSignature(us.id, e.jobNumber, e.name, us.fileName, us.userId, us.base64) from EmployeeInfo e
        left join UserSignature us on e.jobNumber = us.jobNumber
        where e.isExit = false
        order by cast(e.jobNumber as integer )
""")
    List<UserSignature> findAllUserSignatures();

    @Transactional
    void deleteByJobNumber(@Size(max = 255) String jobNumber);
}