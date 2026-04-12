package com.github.ChelovekVreditel.chinese_cars.repositories.ConfigurationOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;

import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigurationOptionCustomRepositoryImpl implements ConfigurationOptionCustomRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void batchUpsert(List<ConfigurationOption> options) {
        String sql = """
            INSERT INTO configuration_options (configuration_id, category, original_name, name, value, price_cny)
            VALUES (:configurationId, :category, :originalName, :name, :value, :priceCny)
            ON CONFLICT (configuration_id, original_name) DO UPDATE SET 
                category = EXCLUDED.category,
                value = EXCLUDED.value,
                price_cny = EXCLUDED.price_cny
            WHERE configuration_options.category IS DISTINCT FROM EXCLUDED.category
                OR configuration_options.value IS DISTINCT FROM EXCLUDED.value
                OR configuration_options.price_cny IS DISTINCT FROM EXCLUDED.price_cny;
        """;

        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(options);
        namedJdbcTemplate.batchUpdate(sql, batch);
    }

    @Override
    public List<Long> findIdsByConfigurationIdAndOriginalName(List<SimpleEntry<Long, String>> characteristics) {
        if (characteristics == null || characteristics.isEmpty()) {
            return List.of();
        }
        
        String conditions = characteristics.stream()
            .map(entry -> "(configuration_id = ? AND original_name = ?)")
            .collect(Collectors.joining(" OR "));
        
        String sql = "SELECT id FROM configuration_options WHERE " + conditions;
        
        List<Object> params = new ArrayList<>();
        for (Map.Entry<Long, String> entry : characteristics) {
            params.add(entry.getKey());
            params.add(entry.getValue());
        }
        
        return jdbcTemplate.queryForList(sql, Long.class, params.toArray());
    }
} 
