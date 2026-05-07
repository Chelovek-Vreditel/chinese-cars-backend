package com.github.ChelovekVreditel.chinese_cars.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.github.ChelovekVreditel.chinese_cars.enums.AgeCategory;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("recycling_fee_rates")
public record RecyclingFeeRate(

    @Id
    Long id,

    BigDecimal baseRateRub,
    AgeCategory ageCategory,
    Integer engineVolumeFromCc,     // NULL для электромобилей
    Integer engineVolumeToCc,       // NULL для электромобилей
    BigDecimal enginePowerFromKw,
    BigDecimal enginePowerToKw,     // NULL = без ограничений
    BigDecimal coefficient,
    Boolean isPreferential,
    LocalDate validFrom,
    LocalDate validTo
) {}
