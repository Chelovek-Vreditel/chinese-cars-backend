package com.github.ChelovekVreditel.chinese_cars.repositories.CarsConfigurationsUpdateTimes;

import com.github.ChelovekVreditel.chinese_cars.models.CarConfigurationUpdateTime;

import org.springframework.data.repository.CrudRepository;

import lombok.NonNull;

public interface CarsConfigurationsUpdateTimesRepository extends CrudRepository<@NonNull CarConfigurationUpdateTime, @NonNull Long>,
                                                                    CarsConfigurationsUpdateTimesCustomRepository {

}
