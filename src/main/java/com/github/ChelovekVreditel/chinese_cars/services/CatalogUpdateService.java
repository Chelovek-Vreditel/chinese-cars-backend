package com.github.ChelovekVreditel.chinese_cars.services;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.ChelovekVreditel.chinese_cars.dtos.ConfigurationDetails;
import com.github.ChelovekVreditel.chinese_cars.models.Car;
import com.github.ChelovekVreditel.chinese_cars.models.CarConfiguration;
import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationOption;
import com.github.ChelovekVreditel.chinese_cars.repositories.CarsUpdateTimesRepository;
import com.github.ChelovekVreditel.chinese_cars.repositories.CarConfigurations.CarConfigurationRepository;
import com.github.ChelovekVreditel.chinese_cars.repositories.Cars.CarRepository;
import com.github.ChelovekVreditel.chinese_cars.repositories.CarsConfigurationsUpdateTimes.CarsConfigurationsUpdateTimesRepository;
import com.github.ChelovekVreditel.chinese_cars.repositories.ConfigurationOptions.ConfigurationOptionRepository;
import com.github.ChelovekVreditel.chinese_cars.repositories.ConfigurationOptionsUpdateTimes.ConfigurationOptionsUpdateTimesRepository;
import com.github.ChelovekVreditel.chinese_cars.utils.AudiParser;
import com.github.ChelovekVreditel.chinese_cars.utils.Translator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CatalogUpdateService {

    private final CarRepository carRepository;
    private final CarsUpdateTimesRepository carsUpdateRepository;
    private final CarConfigurationRepository carConfigurationRepository;
    private final CarsConfigurationsUpdateTimesRepository carConfigurationTimeRepository;
    private final ConfigurationOptionRepository configurationOptionRepository;
    private final ConfigurationOptionsUpdateTimesRepository configurationOptionTimeRepository;

    @Value("${external.url.Audi.models}")
    private String urlAudiModels;
    @Value("${external.url.Audi.models_specific_part}")
    private String urlAudiModelsSpecificPart;
    @Value("${external.url.Audi.base}")
    private String urlAudiBase;
    private final AudiParser audiParser;

    private final Translator translator; 

    public void updateAudiCatalog() {
        List<Car> audiModels; 
        try {
            audiModels = audiParser.extractCarsModels(urlAudiModels, urlAudiModelsSpecificPart, urlAudiBase);
        } catch (ConnectException | SocketTimeoutException ne) {
            System.err.println("Не удалось подключиться к сайту Audi: " + ne.getMessage());
            audiModels = List.of();
        } catch (IOException ioe) {
            System.err.println("Произошла непревиденная ошибка на этапе парсинга: " + ioe.getMessage());
            audiModels = List.of();
        }
        for (Car audiModel : audiModels) {
            try {
                translator.translateCar(audiModel);
                List<ConfigurationDetails> modelDetails = audiParser.extractConfigurationsDetails(audiModel.getSourceUrl());
                for (ConfigurationDetails details : modelDetails) {
                    translator.translateConfigurationDetails(details);
                }
                saveCarData(audiModel, modelDetails);
            } catch (Exception e) {
                System.err.println("Получена ошибка при обновлении каталога Audi: " + e.getMessage());
                e.printStackTrace();
            }
        }
    } 

    @Transactional
    protected void saveCarData(Car car, List<ConfigurationDetails> configurationDetails) throws Exception {
        carRepository.upsert(car.getBrand(), car.getSeries(), car.getOriginalModel(), car.getModel(),
                                car.getBasePriceCny(), car.getDescription(), car.getSourceUrl());
        Long carId = carRepository.findIdByBrandAndOriginalModel(car.getBrand(), car.getOriginalModel())
            .orElseThrow(() -> new Exception("Не был найден объект Car после сохранения в БД."));
        carsUpdateRepository.upsert(carId);
        List<CarConfiguration> carConfigurations = configurationDetails.stream()
            .map(ConfigurationDetails::getCarConfiguration)
            .collect(Collectors.toList());
        for (CarConfiguration configuration : carConfigurations) {
            configuration.setCarId(carId);
        }
        carConfigurationRepository.batchUpsert(carConfigurations);
        List<Long> configurationIds = new ArrayList<>();
        List<ConfigurationOption> options = new ArrayList<>();
        for (ConfigurationDetails details : configurationDetails) {
           Long confId = carConfigurationRepository.findIdByCarIdAndOriginalName(carId, details.getCarConfiguration().getOriginalName())
               .orElseThrow(() -> new Exception("Не был найден объект CarConfiguration после сохранения в БД."));
           configurationIds.add(confId);
           for (ConfigurationOption option : details.getConfigurationOptions()) {
               option.setConfigurationId(confId);
               options.add(option);
           }
        }
        carConfigurationTimeRepository.batchUpsert(configurationIds);
        configurationOptionRepository.batchUpsert(options);
        List<SimpleEntry<Long, String>> searchingProperties = options.stream()
            .map(o -> new AbstractMap.SimpleEntry<>(o.getConfigurationId(), o.getOriginalName()))
            .toList();
        List<Long> optionsIds = configurationOptionRepository.findIdsByConfigurationIdAndOriginalName(searchingProperties);
        if (optionsIds.isEmpty()) throw new Exception("Не были найдены объекты ConfigurationOption после сохранения в БД.");
        configurationOptionTimeRepository.batchUpsert(optionsIds);
    }
}
