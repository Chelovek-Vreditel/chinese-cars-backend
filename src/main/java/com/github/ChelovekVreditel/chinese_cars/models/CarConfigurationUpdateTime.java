package com.github.ChelovekVreditel.chinese_cars.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CarConfigurationUpdateTime {

    @Id
    private Long carConfigurationId;

    private LocalDateTime updatedAt;
}
