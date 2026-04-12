package com.github.ChelovekVreditel.chinese_cars.repositories.CarConfigurations;

import java.util.List;

import com.github.ChelovekVreditel.chinese_cars.models.CarConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

@Repository
public class CarConfigurationCustomRepositoryImpl implements CarConfigurationCustomRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void batchUpsert(List<CarConfiguration> configurations) {
        String sql = """
            INSERT INTO cars_configurations (car_id, original_name, name, base_price_cny)
            VALUES (:carId, :originalName, :name, :basePriceCny)
            ON CONFLICT (car_id, original_name) DO UPDATE SET 
                base_price_cny = EXCLUDED.base_price_cny
            WHERE cars_configurations.base_price_cny IS DISTINCT FROM EXCLUDED.base_price_cny
        """;

        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(configurations);
        jdbcTemplate.batchUpdate(sql, batch);
    }

}
