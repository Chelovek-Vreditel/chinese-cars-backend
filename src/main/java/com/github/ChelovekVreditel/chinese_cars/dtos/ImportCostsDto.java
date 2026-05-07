package com.github.ChelovekVreditel.chinese_cars.dtos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ImportCostsDto {
    private BigDecimal customsDutyRub;
    private BigDecimal customsFeeRub;
    private BigDecimal recyclingFeeRub;
    private BigDecimal exciseRub;       // NULL для ДВС
    private BigDecimal vatRub;          // NULL для ДВС
    private BigDecimal totalImportCostRub;
}
