package com.github.ChelovekVreditel.chinese_cars.services;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import com.github.ChelovekVreditel.chinese_cars.dtos.ConfigurationDetails;
import com.github.ChelovekVreditel.chinese_cars.enums.CarBrand;
import com.github.ChelovekVreditel.chinese_cars.models.Car;
import com.github.ChelovekVreditel.chinese_cars.models.CarConfiguration;
import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationOption;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class CatalogUpdateServiceTest {

    @SuppressWarnings("resource")
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18.3-alpine3.23")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");

        registry.add("external.url.Audi.models", () -> "https://www.audi.cn/zh/models.html");
        registry.add("external.url.Audi.models_specific_part", () -> "/zh/models");
        registry.add("external.url.Audi.base", () -> "https://www.audi.cn/zh/performanceequipment.");
    }

    @Autowired
    private CatalogUpdateService catalogUpdateService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldInsertIntoDBNewRecords() {
        Car car = Car.builder()
            .brand(CarBrand.Audi)
            .series("A")
            .model("Comfort A")
            .basePriceCny(new BigDecimal("1200.00"))
            .description("Крутая, комфортная для всей семьи.")
            .sourceUrl("http://some.fake.url")
            .build();
        CarConfiguration conf1 = CarConfiguration.builder()
            .name("EXTREME A")
            .basePriceCny(new BigDecimal("1200.00"))
            .build();
        CarConfiguration conf2 = CarConfiguration.builder()
            .name("LOW PRICE A")
            .basePriceCny(new BigDecimal("980.99"))
            .build();
        ConfigurationOption option1_1 = ConfigurationOption.builder()
            .category("Внутренности")
            .name("Двигатель V8")
            .value("included")
            .build();
        ConfigurationOption option1_2 = ConfigurationOption.builder()
            .category("Интерьер")
            .name("Сидения с подогревом")
            .value("is_optional")
            .priceCny(new BigDecimal("100.80"))
            .build();
        ConfigurationOption option2_1 = ConfigurationOption.builder()
            .category("Внутренности")
            .name("Двигатель V8")
            .value("included")
            .build();
        ConfigurationOption option2_2 = ConfigurationOption.builder()
            .category("Интерьер")
            .name("Сидения с подогревом")
            .value("none")
            .build();

        ConfigurationDetails details1 = ConfigurationDetails.builder()
            .carConfiguration(conf1)
            .configurationOptions(List.of(option1_1, option1_2))
            .build();
        ConfigurationDetails details2 = ConfigurationDetails.builder()
            .carConfiguration(conf2)
            .configurationOptions(List.of(option2_1, option2_2))
            .build();

        Integer expectedCarNumber = 1;
        Integer expectedConfigurationNumber = 2;
        Integer expectedOptionsNumber = 4;

        try {
            catalogUpdateService.saveCarData(car, List.of(details1, details2));
        } catch (Exception e) {
            System.err.println("Возникла ошибка в результате сохранения данных в БД.");
        }

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cars", Integer.class);
        assertEquals(expectedCarNumber, count);
        count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cars_update_times", Integer.class);
        assertEquals(expectedCarNumber, count);

        count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cars_configurations", Integer.class);
        assertEquals(expectedConfigurationNumber, count);
        count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cars_configurations_update_times", Integer.class);
        assertEquals(expectedConfigurationNumber, count);

        count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM configuration_options", Integer.class);
        assertEquals(expectedOptionsNumber, count);
        count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM configuration_options_update_times", Integer.class);
        assertEquals(expectedOptionsNumber, count);
    }
}
