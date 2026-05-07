package com.github.ChelovekVreditel.chinese_cars.repositories;

import java.time.LocalDate;
import java.util.Optional;

import com.github.ChelovekVreditel.chinese_cars.models.ExciseRate;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ExciseRateRepository
        extends CrudRepository<ExciseRate, Long> {

    @Query("""
        SELECT * FROM excise_rates
        WHERE engine_power_from_hp <= :enginePowerHp
          AND (engine_power_to_hp IS NULL OR engine_power_to_hp >= :enginePowerHp)
          AND valid_from <= :date
          AND (valid_to IS NULL OR valid_to >= :date)
        LIMIT 1
        """)
    Optional<ExciseRate> findRate(
            @Param("enginePowerHp") int enginePowerHp,
            @Param("date") LocalDate date
    );
}
