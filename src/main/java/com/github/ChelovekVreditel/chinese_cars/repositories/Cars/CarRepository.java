package com.github.ChelovekVreditel.chinese_cars.repositories.Cars;

import java.math.BigDecimal;
import java.util.Optional;

import com.github.ChelovekVreditel.chinese_cars.enums.CarBrand;
import com.github.ChelovekVreditel.chinese_cars.models.Car;
import lombok.NonNull;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CarRepository extends CrudRepository<@NonNull Car, @NonNull Long>, CarCustomRepository {

    @Modifying
    @Transactional
    @Query("""
        INSERT INTO cars (brand, series, model, base_price_cny, description, source_url)
        VALUES (:brand, :series, :model, :basePriceCny, :description, :sourceUrl)
        ON CONFLICT (brand, model) DO UPDATE SET 
            series = EXCLUDED.series,
            base_price_cny = EXCLUDED.base_price_cny,
            description = EXCLUDED.description,
            source_url = EXCLUDED.source_url
        WHERE cars.series IS DISTINCT FROM EXCLUDED.series
            OR cars.base_price_cny IS DISTINCT FROM EXCLUDED.base_price_cny
            OR cars.description IS DISTINCT FROM EXCLUDED.description
            OR cars.source_url IS DISTINCT FROM EXCLUDED.source_url
    """)
    public void upsert(@Param("brand") CarBrand brand,
                @Param("series") String series,
                @Param("model") String model,
                @Param("basePriceCny") BigDecimal basePriceCny,
                @Param("description") String description,
                @Param("sourceUrl") String sourceUrl);

    @Query("SELECT id FROM cars WHERE brand = :brand AND model = :model")
    public Optional<Long> findIdByBrandAndModel(@Param("brand") CarBrand brand,
                                                @Param("model") String model);
}
