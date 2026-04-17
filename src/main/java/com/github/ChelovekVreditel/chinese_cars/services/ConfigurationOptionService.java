package com.github.ChelovekVreditel.chinese_cars.services;

import java.util.List;

import com.github.ChelovekVreditel.chinese_cars.dtos.ConfigurationOptionDto;
import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationOption;
import com.github.ChelovekVreditel.chinese_cars.repositories.ConfigurationOptions.ConfigurationOptionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationOptionService {

    @Autowired
    private ConfigurationOptionRepository optionRepository;

    public List<ConfigurationOptionDto> getOptionsByConfigurationId(Long confId) throws Exception {
        List<ConfigurationOption> repositoryResponse = optionRepository.getConfigurationOptionsByConfigurationId(confId);
        if (repositoryResponse == null || repositoryResponse.isEmpty()) {
            throw new Exception("Не было найдено ни одной опции для конфигурации с id " + confId);
        }
        List<ConfigurationOptionDto> result = repositoryResponse.stream()
            .map(option -> new ConfigurationOptionDto(option))
            .toList();
        return result;
    }
}
