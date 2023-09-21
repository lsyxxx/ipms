package com.abt.sys.repository;

import com.abt.sys.model.entity.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 */
public interface UploadFileRepository  extends JpaRepository<UploadFile, String> {
}
