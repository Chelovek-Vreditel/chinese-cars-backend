package com.github.ChelovekVreditel.chinese_cars.models;

import com.github.ChelovekVreditel.chinese_cars.enums.CarBrand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("cars")
public class Car {

    @Id
    private Long id;

    private CarBrand brand;
    private String series;
    private String model;
    private BigDecimal basePriceCny;
    private String description;
    private String sourceUrl;
    private LocalDateTime createdDt;
    private LocalDateTime updatedAt;
}
