package com.github.ChelovekVreditel.chinese_cars.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import com.github.ChelovekVreditel.chinese_cars.models.CustomsFeeRate;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CustomsFeeRateRepository
        extends CrudRepository<CustomsFeeRate, Long> {

    @Query("""
        SELECT * FROM customs_fee_rates
        WHERE customs_value_from_rub <= :customsValueRub
          AND (customs_value_to_rub IS NULL OR customs_value_to_rub >= :customsValueRub)
          AND valid_from <= :date
          AND (valid_to IS NULL OR valid_to >= :date)
        LIMIT 1
        """)
    Optional<CustomsFeeRate> findRate(
            @Param("customsValueRub") BigDecimal customsValueRub,
            @Param("date") LocalDate date
    );
}
