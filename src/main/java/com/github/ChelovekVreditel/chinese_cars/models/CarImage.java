package com.github.ChelovekVreditel.chinese_cars.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("car_images")
public class CarImage {

    @Id
    private Long id;

    private Long carId;
    private String storageKey;
    private String sourceUrl;
    private String contentType;
}
