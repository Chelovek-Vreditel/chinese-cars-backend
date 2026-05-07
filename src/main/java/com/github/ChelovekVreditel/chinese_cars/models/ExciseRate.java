package com.github.ChelovekVreditel.chinese_cars.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("excise_rates")
public record ExciseRate(

    @Id
    Long id,

    Integer enginePowerFromHp,
    Integer enginePowerToHp,    // NULL = без ограничений
    BigDecimal rateRubPerHp,
    LocalDate validFrom,
    LocalDate validTo
) {}
