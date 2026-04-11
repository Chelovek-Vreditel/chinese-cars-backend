package com.github.ChelovekVreditel.chinese_cars.repositories.ConfigurationOptionsUpdateTimes;

import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationOptionUpdateTime;

import org.springframework.data.repository.CrudRepository;

import lombok.NonNull;

public interface ConfigurationOptionsUpdateTimesRepository extends CrudRepository<@NonNull ConfigurationOptionUpdateTime, @NonNull Long>,
                                                                ConfigurationOptionsUpdateTimesCustomRepository
{

}
