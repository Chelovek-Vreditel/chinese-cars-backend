package com.github.ChelovekVreditel.chinese_cars.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CalculationRequest {
    private Long configurationId;
    private List<Long> optionIds;
}
