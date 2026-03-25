package com.github.ChelovekVreditel.chinese_cars.repositories;

import com.github.ChelovekVreditel.chinese_cars.models.CarConfiguration;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarConfigurationRepository extends CrudRepository<@NonNull CarConfiguration, @NonNull Long> {
}
