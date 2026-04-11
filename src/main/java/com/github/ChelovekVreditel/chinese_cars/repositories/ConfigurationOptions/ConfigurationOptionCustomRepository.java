package com.github.ChelovekVreditel.chinese_cars.repositories.ConfigurationOptions;

import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationOption;

public interface ConfigurationOptionCustomRepository {

    public void batchUpsert(List<ConfigurationOption> options);
    public List<Long> findIdsByConfigurationIdAndName(List<SimpleEntry<Long, String>> characteristics);
}
