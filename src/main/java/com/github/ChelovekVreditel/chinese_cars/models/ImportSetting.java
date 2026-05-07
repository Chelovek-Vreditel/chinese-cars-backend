package com.github.ChelovekVreditel.chinese_cars.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("import_settings")
public record ImportSetting(

    @Id
    String key,

    String value,
    String description,
    LocalDateTime updatedAt
) {}
