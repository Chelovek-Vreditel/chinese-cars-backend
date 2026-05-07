package com.github.ChelovekVreditel.chinese_cars.models;

import java.math.BigDecimal;

import com.github.ChelovekVreditel.chinese_cars.enums.EngineType;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Table("configuration_technical_specs")
@Data
public class ConfigurationTechnicalSpecs {

    @Id
    private Long configurationId;
    private Integer engineVolumeCc;
    private Integer enginePowerHp;
    private BigDecimal enginePowerKw;
    private EngineType engineType;
    private Integer manufactureYear;
    private String tnvedCode;
}
