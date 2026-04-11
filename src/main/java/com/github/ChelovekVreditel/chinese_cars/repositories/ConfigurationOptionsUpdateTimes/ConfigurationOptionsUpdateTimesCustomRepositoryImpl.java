package com.github.ChelovekVreditel.chinese_cars.repositories.ConfigurationOptionsUpdateTimes;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigurationOptionsUpdateTimesCustomRepositoryImpl implements ConfigurationOptionsUpdateTimesCustomRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void batchUpsert(List<Long> ids) {
        String sql = """
            INSERT INTO configuration_options_update_times (configuration_option_id, updated_at)
            VALUES (:id, NOW())
            ON CONFLICT (configuration_option_id) DO UPDATE SET 
                updated_at = EXCLUDED.updated_at;
        """;

        SqlParameterSource[] batch = ids.stream()
            .map(id -> new MapSqlParameterSource("id", id))
            .toArray(SqlParameterSource[]::new);
        jdbcTemplate.batchUpdate(sql, batch);
    }

}
