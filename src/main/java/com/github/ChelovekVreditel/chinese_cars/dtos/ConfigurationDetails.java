package com.github.ChelovekVreditel.chinese_cars.dtos;

import com.github.ChelovekVreditel.chinese_cars.models.CarConfiguration;
import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationDetails {

    private CarConfiguration carConfiguration;
    @Builder.Default
    private List<ConfigurationOption> configurationOptions = new ArrayList<>();
}
