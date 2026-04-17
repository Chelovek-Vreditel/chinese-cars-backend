package com.github.ChelovekVreditel.chinese_cars.dtos;

import java.math.BigDecimal;

import com.github.ChelovekVreditel.chinese_cars.models.Car;

import lombok.Data;

@Data
public class CarDto {

    private Long id;
    private String series;
    private String model;
    private BigDecimal basePrice;
    private String description;
    private String sourceUrl;

    public CarDto(Car car) {
        this.id = car.getId();
        this.series = car.getSeries();
        this.model = car.getModel();
        this.basePrice = car.getBasePriceCny();
        this.description = car.getDescription();
        this.sourceUrl = car.getSourceUrl();
    }
}
