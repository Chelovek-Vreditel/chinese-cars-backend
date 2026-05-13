package com.github.ChelovekVreditel.chinese_cars.services;

import java.util.Collections;
import java.util.List;

import com.github.ChelovekVreditel.chinese_cars.dtos.CalculationRequest;
import com.github.ChelovekVreditel.chinese_cars.dtos.CalculationResponse;
import com.github.ChelovekVreditel.chinese_cars.dtos.CarDto;
import com.github.ChelovekVreditel.chinese_cars.enums.CarBrand;
import com.github.ChelovekVreditel.chinese_cars.models.Car;
import com.github.ChelovekVreditel.chinese_cars.repositories.CarConfigurations.CarConfigurationRepository;
import com.github.ChelovekVreditel.chinese_cars.repositories.Cars.CarRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CarService {

    @Autowired
    private CarRepository carRepository;
    @Autowired
    private CurrencyConverter converter;
    @Autowired
    private CalculationFacade calculationFacade;
    @Autowired
    private CarConfigurationRepository carConfigurationRepository;

    public List<CarDto> getCarsByBrand(String brand) throws Exception {
        List<Car> rawCars = switch (brand) {
            case "Audi" -> carRepository.getCarsByBrand(CarBrand.Audi);
            default -> throw new Exception("Бренд автомобилей " + brand + " не существует или он не добавлен в сервис.");
        };
        List<CarDto> result = rawCars.stream()
            .map(car -> {
                CarDto carDto = new CarDto(car);
                carDto.setBasePrice(converter.convertCnyToRub(carDto.getBasePrice()));
                Long confId = carConfigurationRepository.getFirstConfigurationIdByCarId(carDto.getId()).get();
                if (confId != null) {
                    try {
                        CalculationRequest request = new CalculationRequest(confId, Collections.emptyList());
                        CalculationResponse calculations = calculationFacade.calculate(request);
                        carDto.setFinalCost(calculations.getTotalCostRub());
                    } catch (Exception e) {
                        log.error("Произошла ошибка: " + e.getMessage());
                    }
                }
                return carDto;
            })
            .toList();
        return result;
    }
}
