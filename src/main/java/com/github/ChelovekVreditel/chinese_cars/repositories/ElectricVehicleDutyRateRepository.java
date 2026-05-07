package com.github.ChelovekVreditel.chinese_cars.repositories;

import java.time.LocalDate;
import java.util.Optional;

import com.github.ChelovekVreditel.chinese_cars.models.ElectricVehicleDutyRate;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ElectricVehicleDutyRateRepository
        extends CrudRepository<ElectricVehicleDutyRate, Long> {

    @Query("""
        SELECT * FROM electric_vehicle_duty_rates
        WHERE valid_from <= :date
          AND (valid_to IS NULL OR valid_to >= :date)
        LIMIT 1
        """)
    Optional<ElectricVehicleDutyRate> findCurrentRate(@Param("date") LocalDate date);
}
