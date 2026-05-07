package com.github.ChelovekVreditel.chinese_cars.dtos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SelectedOptionDto {
    private Long id;
    private String name;
    private BigDecimal priceRub;
}
