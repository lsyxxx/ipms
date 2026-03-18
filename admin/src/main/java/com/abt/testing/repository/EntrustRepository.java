package com.abt.testing.repository;

import com.abt.testing.entity.Entrust;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface EntrustRepository extends JpaRepository<Entrust, String> {
  List<Entrust> findByhtBianHao(String htBianHao);

  List<Entrust> findByIdIn(Collection<String> ids);
}