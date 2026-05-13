package com.github.ChelovekVreditel.chinese_cars.models;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("recycling_fee_rates")
public record RecyclingFeeRate(

    @Id
    Long id,

    Boolean isElectrical,
    BigDecimal engineVolumeLFrom,
    BigDecimal engineVolumeLTo,
    Integer enginePowerHpFrom,
    Integer enginePowerHpTo,
    BigDecimal coefLess,
    BigDecimal coefMore
) {}
