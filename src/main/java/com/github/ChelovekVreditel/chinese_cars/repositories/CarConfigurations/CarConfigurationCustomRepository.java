package com.github.ChelovekVreditel.chinese_cars.repositories.CarConfigurations;

import java.util.List;

import com.github.ChelovekVreditel.chinese_cars.models.CarConfiguration;

public interface CarConfigurationCustomRepository {
    void batchUpsert(List<CarConfiguration> configurations);
}
