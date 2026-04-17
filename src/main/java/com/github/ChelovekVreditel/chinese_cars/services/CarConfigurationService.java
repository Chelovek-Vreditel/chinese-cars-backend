package com.github.ChelovekVreditel.chinese_cars.services;

import java.util.List;

import com.github.ChelovekVreditel.chinese_cars.dtos.CarConfigurationDto;
import com.github.ChelovekVreditel.chinese_cars.models.CarConfiguration;
import com.github.ChelovekVreditel.chinese_cars.repositories.CarConfigurations.CarConfigurationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarConfigurationService {

    @Autowired
    private CarConfigurationRepository configurationRepository;

    public List<CarConfigurationDto> getConfiguartionsByCarId(Long carId) throws Exception {
        List<CarConfiguration> repositoryResponse = configurationRepository.getCarConfigurationsByCarId(carId);
        if (repositoryResponse == null || repositoryResponse.isEmpty()) {
            throw new Exception("Не было найдено ни одной конфигурации для модели с id " + carId);
        }
        List<CarConfigurationDto> result = repositoryResponse.stream()
            .map(conf -> new CarConfigurationDto(conf))
            .toList();
        return result;
    }
}
