package com.abt.wf.repository;

import com.abt.wf.entity.UserSignature;
import com.abt.wf.model.PurchasePdfSignatureFile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

/** 新版采购 PDF 专用签名查询；旧版签名 Repository 与调用逻辑保持原样。 */
public interface PurchasePdfSignatureRepository extends Repository<UserSignature, String> {
    @Query("SELECT new com.abt.wf.model.PurchasePdfSignatureFile(s.userId, s.fileName) "
            + "FROM UserSignature s WHERE s.userId IN :userIds")
    List<PurchasePdfSignatureFile> findList(List<String> userIds);
}
