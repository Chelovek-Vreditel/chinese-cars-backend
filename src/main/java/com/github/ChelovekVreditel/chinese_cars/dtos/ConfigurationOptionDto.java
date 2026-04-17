package com.github.ChelovekVreditel.chinese_cars.dtos;

import java.math.BigDecimal;

import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationOption;

import lombok.Data;

@Data
public class ConfigurationOptionDto {

    private Long id;
    private String category;
    private String name;
    private String value;
    private BigDecimal price;

    public ConfigurationOptionDto(ConfigurationOption option) {
        this.id = option.getId();
        this.category = option.getCategory();
        this.name = option.getName();
        this.value = option.getValue();
        this.price = option.getPriceCny();
    }
}
