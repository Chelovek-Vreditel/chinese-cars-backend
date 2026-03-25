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
@Table("configuration_options")
public class ConfigurationOption {

    @Id
    private Long id;

    private Long configurationId;
    private String category;
    private String name;
    private String value;
    private BigDecimal priceCny;
}
