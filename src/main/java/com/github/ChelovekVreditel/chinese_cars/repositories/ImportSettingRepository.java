package com.github.ChelovekVreditel.chinese_cars.repositories;

import java.math.BigDecimal;
import java.util.Optional;

import com.github.ChelovekVreditel.chinese_cars.models.ImportSetting;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ImportSettingRepository
        extends CrudRepository<ImportSetting, String> {

    @Query("""
        SELECT value FROM import_settings
        WHERE key = :key
        LIMIT 1
    """)
    public Optional<BigDecimal> getByKey(@Param("key") String key);
}
