package com.github.ChelovekVreditel.chinese_cars.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("customs_fee_rates")
public record CustomsFeeRate(

    @Id
    Long id,

    BigDecimal customsValueFromRub,
    BigDecimal customsValueToRub,   // NULL = без ограничений
    BigDecimal feeRub,
    LocalDate validFrom,
    LocalDate validTo
) {}
