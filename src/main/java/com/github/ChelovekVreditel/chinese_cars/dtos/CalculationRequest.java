package com.github.ChelovekVreditel.chinese_cars.dtos;

import java.util.List;

import lombok.Data;

@Data
public class CalculationRequest {
    private Long configurationId;
    private List<Long> optionIds;
}
