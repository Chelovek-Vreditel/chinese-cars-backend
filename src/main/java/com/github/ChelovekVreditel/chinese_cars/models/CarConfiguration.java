package com.github.ChelovekVreditel.chinese_cars.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("cars_configurations")
public class CarConfiguration {

    @Id
    private Long id;

    private Long carId;
    private String name;
    private BigDecimal basePriceCny;
}
