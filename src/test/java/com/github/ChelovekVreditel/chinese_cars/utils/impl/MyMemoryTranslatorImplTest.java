package com.github.ChelovekVreditel.chinese_cars.utils.impl;

import java.util.List;

import com.github.ChelovekVreditel.chinese_cars.dtos.ConfigurationDetails;
import com.github.ChelovekVreditel.chinese_cars.models.CarConfiguration;
import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationOption;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
public class MyMemoryTranslatorImplTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("USE_MYMEMORY_EMAIL", () -> "false");
        registry.add("MYMEMORY_EMAIL", () -> "");
    }

    @Autowired
    private MyMemoryTranslatorImpl translator;

    @Test
    public void translateConfigurationDetailsTest() {
       CarConfiguration carConfiguration = CarConfiguration.builder()
           .originalName("A3 Sportback 35TFSI 飞驰悦享型")
           .build();
        ConfigurationOption option1 = ConfigurationOption.builder()
            .category("发动机")
            .originalName("发动机型式")
            .value("四缸16气门1.5L 汽油直喷涡轮增压发动机")
            .build();
        ConfigurationOption option2 = ConfigurationOption.builder()
            .category("动力传动系统")
            .originalName("变速器")
            .value("7速 S tronic®双离合变速器")
            .build();
        ConfigurationOption option3 = ConfigurationOption.builder()
            .category("车身/外饰")
            .originalName("静音前风挡带灰色陶瓷边")
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
