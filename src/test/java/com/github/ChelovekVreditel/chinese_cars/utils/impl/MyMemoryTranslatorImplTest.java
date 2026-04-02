package com.github.ChelovekVreditel.chinese_cars.utils.impl;

import java.util.List;

import com.github.ChelovekVreditel.chinese_cars.dtos.ConfigurationDetails;
import com.github.ChelovekVreditel.chinese_cars.models.CarConfiguration;
import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationOption;

import org.junit.jupiter.api.Test;

public class MyMemoryTranslatorImplTest {

    private final MyMemoryTranslatorImpl translator = new MyMemoryTranslatorImpl();

    @Test
    public void translateConfigurationDetailsTest() {
       CarConfiguration carConfiguration = CarConfiguration.builder()
           .name("A3 Sportback 35TFSI 飞驰悦享型")
           .build();
        ConfigurationOption option1 = ConfigurationOption.builder()
            .category("发动机")
            .name("发动机型式")
            .value("四缸16气门1.5L 汽油直喷涡轮增压发动机")
            .build();
        ConfigurationOption option2 = ConfigurationOption.builder()
            .category("动力传动系统")
            .name("变速器")
            .value("7速 S tronic®双离合变速器")
            .build();
        ConfigurationOption option3 = ConfigurationOption.builder()
            .category("车身/外饰")
            .name("静音前风挡带灰色陶瓷边")
            .value("included")
            .build();
        List<ConfigurationOption> options = List.of(option1, option2, option3);
        ConfigurationDetails configurationDetails = ConfigurationDetails.builder()
            .carConfiguration(carConfiguration)
            .configurationOptions(options)
            .build();

        translator.translateConfigurationDetails(configurationDetails);

        System.out.println("=== КОНФИГУРАЦИЯ ===");
        System.out.println(configurationDetails.getCarConfiguration().toString());
        System.out.println("=== ОПЦИИ ===");
        for (ConfigurationOption option : configurationDetails.getConfigurationOptions()) {
            System.out.println(option.toString());
        }
    }

}
