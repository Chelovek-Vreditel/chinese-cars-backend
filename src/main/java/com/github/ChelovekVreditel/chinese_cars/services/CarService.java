package com.github.ChelovekVreditel.chinese_cars.services;

import java.util.List;

import com.github.ChelovekVreditel.chinese_cars.dtos.CarDto;
import com.github.ChelovekVreditel.chinese_cars.enums.CarBrand;
import com.github.ChelovekVreditel.chinese_cars.models.Car;
import com.github.ChelovekVreditel.chinese_cars.repositories.Cars.CarRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    public List<CarDto> getCarsByBrand(String brand) throws Exception {
        List<Car> rawCars = switch (brand) {
            case "Audi" -> carRepository.getCarsByBrand(CarBrand.Audi);
            default -> throw new Exception("Бренд автомобилей " + brand + " не существует или он не добавлен в сервис.");
        };
        List<CarDto> result = rawCars.stream()
            .map(car -> new CarDto(car))
            .toList();
        return result;
    }
}
