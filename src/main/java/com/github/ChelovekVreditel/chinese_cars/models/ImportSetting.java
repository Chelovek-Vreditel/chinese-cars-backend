package com.github.ChelovekVreditel.chinese_cars.models;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("import_settings")
public record ImportSetting(

    @Id
    String key,

    BigDecimal value
) {}
