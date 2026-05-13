package com.github.ChelovekVreditel.chinese_cars.repositories;

import java.math.BigDecimal;
import java.util.Optional;

import com.github.ChelovekVreditel.chinese_cars.models.RecyclingFeeRate;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RecyclingFeeRateRepository
        extends CrudRepository<RecyclingFeeRate, Long> {

    @Query("""
        SELECT * FROM recycling_fee_rates
        WHERE is_electrical = :isElectrical AND
              engine_power_hp_from <= :enginePowerHp AND engine_power_hp_to > :enginePowerHp AND
              engine_volume_l_from <= :engineVolumeL AND engine_volume_l_to > :engineVolumeL
        LIMIT 1
    """)
    public Optional<RecyclingFeeRate> getRate(
        @Param("isElectrical") Boolean isElectrical,
        @Param("enginePowerHp") Integer enginePowerHp,
        @Param("engineVolumeL") BigDecimal engineVolumeL
    );
}
