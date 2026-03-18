package com.abt.app.respository;

import com.abt.app.entity.AppVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

public interface AppVersionRepository extends JpaRepository<AppVersion, String> {

  @Query("select app from AppVersion app " +
          "where (:platForm is null or :platForm = '' or app.platform like %:platForm%) " +
          "and (:version is null or :version = '' or app.version like %:version%) " +
          "order by app.version desc")
  Page<AppVersion> findBy(String platForm, String version, Pageable pageable);


  AppVersion findByVersion(String version);

}