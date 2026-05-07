package com.github.ChelovekVreditel.chinese_cars.dtos;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class CalculationResponse {
    private BigDecimal baseCarPriceRub;
    private List<SelectedOptionDto> selectedOptions;
    private ImportCostsDto importCosts;
    private BigDecimal totalCostRub;
}
