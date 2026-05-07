package com.github.ChelovekVreditel.chinese_cars.repositories;

import java.time.LocalDate;
import java.util.Optional;

import com.github.ChelovekVreditel.chinese_cars.models.CustomsDutyRate;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CustomsDutyRateRepository
        extends CrudRepository<CustomsDutyRate, Long> {

    @Query("""
        SELECT * FROM customs_duty_rates
        WHERE age_category = :ageCategory
          AND engine_volume_from_cc <= :engineVolumeCc
          AND (engine_volume_to_cc IS NULL OR engine_volume_to_cc >= :engineVolumeCc)
          AND valid_from <= :date
          AND (valid_to IS NULL OR valid_to >= :date)
        LIMIT 1
        """)
    Optional<CustomsDutyRate> findRate(
            @Param("ageCategory") String ageCategory,
            @Param("engineVolumeCc") int engineVolumeCc,
            @Param("date") LocalDate date
    );
}
