package com.github.ChelovekVreditel.chinese_cars.repositories;

import java.math.BigDecimal;
import java.util.Optional;

import com.github.ChelovekVreditel.chinese_cars.models.CustomsFeeRate;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CustomsFeeRateRepository
        extends CrudRepository<CustomsFeeRate, Long> {

    @Query("""
        SELECT * FROM customs_fee_rates
        WHERE cost_rub_from <= :customsValueRub
          AND (cost_rub_to >= :customsValueRub)
        LIMIT 1
        """)
    Optional<CustomsFeeRate> findRate(
            @Param("customsValueRub") BigDecimal customsValueRub
    );
}
