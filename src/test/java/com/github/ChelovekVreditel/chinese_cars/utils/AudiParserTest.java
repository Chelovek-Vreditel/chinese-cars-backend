package com.github.ChelovekVreditel.chinese_cars.utils;

import com.github.ChelovekVreditel.chinese_cars.dtos.CarWithImageSource;
import com.github.ChelovekVreditel.chinese_cars.dtos.ConfigurationDetails;
import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationOption;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class AudiParserTest {

    private final AudiParser audiParser = new AudiParser();

    @Test
    void extractCarsModelsTest() throws IOException {
        List<CarWithImageSource> extractedCarsModels = audiParser.extractCarsModels(
            "https://www.audi.cn/zh/models.html",
            "/zh/models",
            "https://www.audi.cn/zh/performanceequipment."
        );
        for (CarWithImageSource carWithImage : extractedCarsModels) {
            System.out.println("============ МОДЕЛЬ ============");
            System.out.println(carWithImage.car().toString());
            System.out.println(carWithImage.imgUrl());
        }
    }

    @Test
    void extractConfigurationsDetailsTest() throws IOException {
        List<ConfigurationDetails> extractedConfigurationsDetails = audiParser.extractConfigurationsDetails(
                "https://www.audi.cn/zh/performanceequipment.@a@a3@a3_sportback.html?a3_sportback"
        );
        for (ConfigurationDetails details : extractedConfigurationsDetails) {
            System.out.println("========== КОНФИГУРАЦИЯ ==========");
            System.out.println(details.getCarConfiguration().toString());
            System.out.println("Опции:");
            for (ConfigurationOption option : details.getConfigurationOptions()) {
                System.out.println(option.toString());
            }
        }
    }
}
