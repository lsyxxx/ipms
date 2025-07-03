package com.abt.market.repository;

import com.abt.market.entity.TestItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestItemRepository extends JpaRepository<TestItem, String> {
  List<TestItem> findByMid(String mid);

  void deleteByMid(String mid);
}