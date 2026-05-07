package com.github.ChelovekVreditel.chinese_cars.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import com.github.ChelovekVreditel.chinese_cars.models.RecyclingFeeRate;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RecyclingFeeRateRepository
        extends CrudRepository<RecyclingFeeRate, Long> {

    @Query("""
        SELECT * FROM recycling_fee_rates
        WHERE age_category = :ageCategory
          AND is_preferential = :isPreferential
          AND (engine_volume_from_cc IS NULL OR engine_volume_from_cc <= :engineVolumeCc)
          AND (engine_volume_to_cc IS NULL OR engine_volume_to_cc >= :engineVolumeCc)
          AND (engine_power_from_kw IS NULL OR engine_power_from_kw <= :enginePowerKw)
          AND (engine_power_to_kw IS NULL OR engine_power_to_kw >= :enginePowerKw)
          AND valid_from <= :date
          AND (valid_to IS NULL OR valid_to >= :date)
        LIMIT 1
        """)
    Optional<RecyclingFeeRate> findRate(
            @Param("ageCategory") String ageCategory,
            @Param("isPreferential") boolean isPreferential,
            @Param("engineVolumeCc") int engineVolumeCc,
            @Param("enginePowerKw") BigDecimal enginePowerKw,
            @Param("date") LocalDate date
    );
}
