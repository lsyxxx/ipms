package com.abt.oa.reposity;

import com.abt.oa.entity.PaiBan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PaiBanRepository extends JpaRepository<PaiBan, String> {

  List<PaiBan> findByPaibandateBetweenOrderByPaibandateAsc(LocalDate start, LocalDate end);

    List<PaiBan> findByPaibandate(LocalDate paibandate);
}