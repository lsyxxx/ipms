package com.abt.testing.repository;

import com.abt.testing.entity.EnumLib;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnumLibRepository extends JpaRepository<EnumLib, String> {

  /**
   * 根据枚举类型查询所有枚举项
   * @param ftypeid 枚举类型id
   */
  List<EnumLib> findAllByFtypeidOrderByFid(String ftypeid);
}