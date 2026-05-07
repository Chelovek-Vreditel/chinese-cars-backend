package com.github.ChelovekVreditel.chinese_cars.repositories;

import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationTechnicalSpecs;

import org.springframework.data.repository.CrudRepository;

public interface ConfigurationTechnicalSpecsRepository
        extends CrudRepository<ConfigurationTechnicalSpecs, Long> {
}
