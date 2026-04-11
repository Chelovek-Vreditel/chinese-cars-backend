package com.github.ChelovekVreditel.chinese_cars.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

public class ConfigurationOptionUpdateTime {

    @Id
    private Long configurationOptionId;

    private LocalDateTime updatedAt;
}
