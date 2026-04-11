package com.github.ChelovekVreditel.chinese_cars.repositories.CarsConfigurationsUpdateTimes;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class CarsConfigurationsUpdateTimesCustomRepositoryImpl implements CarsConfigurationsUpdateTimesCustomRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void batchUpsert(List<Long> ids) {
        String sql = """
            INSERT INTO cars_configurations_update_times (car_configuration_id, updated_at)
            VALUES (:id, NOW())
            ON CONFLICT (car_configuration_id) DO UPDATE SET 
                updated_at = EXCLUDED.updated_at;
        """;

        SqlParameterSource[] batch = ids.stream()
            .map(id -> new MapSqlParameterSource("id", id))
            .toArray(SqlParameterSource[]::new);
        jdbcTemplate.batchUpdate(sql, batch);
    }
}
