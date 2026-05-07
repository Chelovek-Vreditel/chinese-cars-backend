package com.github.ChelovekVreditel.chinese_cars.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.github.ChelovekVreditel.chinese_cars.enums.AgeCategory;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("customs_duty_rates")
public record CustomsDutyRate(

    @Id
    Long id,

    AgeCategory ageCategory,
    Integer engineVolumeFromCc,
    Integer engineVolumeToCc,   // NULL = без ограничений
    BigDecimal ratePercent,     // NULL для USED_3_5 и USED_5_PLUS
    BigDecimal rateEurPerCc,
    LocalDate validFrom,
    LocalDate validTo           // NULL = действует сейчас
) {}
