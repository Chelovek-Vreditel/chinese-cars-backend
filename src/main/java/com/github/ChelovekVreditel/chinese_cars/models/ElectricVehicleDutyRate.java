package com.github.ChelovekVreditel.chinese_cars.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("electric_vehicle_duty_rates")
public record ElectricVehicleDutyRate(

    @Id
    Long id,

    BigDecimal ratePercent,
    String tnvedCode,
    LocalDate validFrom,
    LocalDate validTo
) {}
