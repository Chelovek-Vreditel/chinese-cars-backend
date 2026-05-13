package com.github.ChelovekVreditel.chinese_cars.models;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("customs_fee_rates")
public record CustomsFeeRate(

    @Id
    Long id,

    BigDecimal costRubFrom,
    BigDecimal costRubTo,
    BigDecimal feeRub
) {}
