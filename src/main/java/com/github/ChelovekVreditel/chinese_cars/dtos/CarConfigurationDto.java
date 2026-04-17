package com.github.ChelovekVreditel.chinese_cars.dtos;

import java.math.BigDecimal;

import com.github.ChelovekVreditel.chinese_cars.models.CarConfiguration;

import lombok.Data;

@Data
public class CarConfigurationDto {

    private Long id;
    private String name;
    private BigDecimal basePrice;

    public CarConfigurationDto(CarConfiguration configuration) {
        this.id = configuration.getId();
        this.name = configuration.getName();
        this.basePrice = configuration.getBasePriceCny();
    }
}
