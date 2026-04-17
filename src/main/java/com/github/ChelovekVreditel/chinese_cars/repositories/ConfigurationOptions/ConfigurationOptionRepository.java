package com.github.ChelovekVreditel.chinese_cars.repositories.ConfigurationOptions;

import java.util.List;

import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationOption;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationOptionRepository extends CrudRepository<@NonNull ConfigurationOption, @NonNull Long>,
                                                    ConfigurationOptionCustomRepository {

    public List<ConfigurationOption> getConfigurationOptionsByConfigurationId(Long confId);
}
