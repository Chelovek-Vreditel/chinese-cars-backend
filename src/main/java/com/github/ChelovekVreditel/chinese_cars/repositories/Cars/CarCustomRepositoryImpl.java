package com.github.ChelovekVreditel.chinese_cars.repositories.Cars;

import java.util.List;

import com.github.ChelovekVreditel.chinese_cars.models.Car;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

@Repository
public class CarCustomRepositoryImpl implements CarCustomRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void batchUpsert(List<Car> cars) {
        String sql = """
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
                OR cars.source_url IS DISTINCT FROM EXCLUDED.source_url;
        """;

        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(cars);
        jdbcTemplate.batchUpdate(sql, batch);
    }

}
