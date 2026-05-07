package com.github.ChelovekVreditel.chinese_cars.repositories;

import com.github.ChelovekVreditel.chinese_cars.models.ImportSetting;

import org.springframework.data.repository.CrudRepository;

public interface ImportSettingRepository
        extends CrudRepository<ImportSetting, String> {
}
