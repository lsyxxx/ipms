package com.abt.liaohe;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 *
 */
@Table(name = "tmp_base")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class TestBaseTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String reportName;
    private LocalDate testDateStart;
    private LocalDate testDateEnd;



}
