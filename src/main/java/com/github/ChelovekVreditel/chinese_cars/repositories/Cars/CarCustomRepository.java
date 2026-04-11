package com.github.ChelovekVreditel.chinese_cars.repositories.Cars;

import java.util.List;

import com.github.ChelovekVreditel.chinese_cars.models.Car;

public interface CarCustomRepository {
    void batchUpsert(List<Car> cars);
}
