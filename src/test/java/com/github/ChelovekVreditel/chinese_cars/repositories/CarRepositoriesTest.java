package com.github.ChelovekVreditel.chinese_cars.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.github.ChelovekVreditel.chinese_cars.configs.JdbcConfig;
import com.github.ChelovekVreditel.chinese_cars.enums.CarBrand;
import com.github.ChelovekVreditel.chinese_cars.models.Car;
import com.github.ChelovekVreditel.chinese_cars.repositories.Cars.CarRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
@Import(JdbcConfig.class)
class CarRepositoriesTest {

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
    }

    @Autowired
    private CarRepository carRepository;
    @Autowired
    private CarsUpdateTimesRepository carTimesRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldInsertNewCarsAndTime() {
        Car car1 = Car.builder()
            .brand(CarBrand.Audi)
            .series("A")
            .model("super A")
            .basePriceCny(new BigDecimal("910.24"))
            .description("Хорошая машина")
            .sourceUrl("http://some.fake.url")
            .build();

        Car car2 = Car.builder()
            .brand(CarBrand.BMW)
            .model("KBM")
            .basePriceCny(new BigDecimal("1400.32"))
            .build();

        carRepository.upsert(car1.getBrand(), car1.getSeries(), car1.getModel(), car1.getBasePriceCny(),
                car1.getDescription(), car1.getSourceUrl());
        carRepository.upsert(car2.getBrand(), car2.getSeries(), car2.getModel(), car2.getBasePriceCny(),
                car2.getDescription(), car2.getSourceUrl());

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cars", Integer.class);
        assertEquals(2, count);
        Long id = carRepository.findIdByBrandAndModel(CarBrand.Audi, "super A").get();
        String savedSeries = jdbcTemplate.queryForObject("SELECT series FROM cars WHERE id = ?", String.class, id);
        assertEquals("A", savedSeries);

        carTimesRepository.upsert(id);
        count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cars_update_times", Integer.class);
        assertEquals(1, count);
    }

    @Test
    void shouldUpdateExistingRecords() {
        Long id = (long) 1;
        CarBrand brand = CarBrand.Audi;
        String series = "A";
        String model = "super";
        BigDecimal basePriceCny = new BigDecimal("1200.20");
        String description = "Изумительна";
        String sourceUrl = "http://some.fake.url";

        jdbcTemplate.update(
            "INSERT INTO cars (id, brand, series, model, base_price_cny, description, source_url) VALUES (?,?,?,?,?,?,?)",
            id, brand.name(), series, model, basePriceCny, null, sourceUrl 
        );

        LocalDateTime time = LocalDateTime.of(2026, 1, 1, 12, 30, 0);

        jdbcTemplate.update(
            "INSERT INTO cars_update_times (car_id, updated_at) VALUES (?,?)",
            id, time
        );

        Car updatedCar = Car.builder()
            .brand(brand)
            .series(series)
            .model(model)
            .basePriceCny(basePriceCny)
            .description(description)
            .sourceUrl(null)
            .build();

        carRepository.upsert(updatedCar.getBrand(), updatedCar.getSeries(), updatedCar.getModel(),
                updatedCar.getBasePriceCny(), updatedCar.getDescription(), updatedCar.getSourceUrl());

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cars", Integer.class); 
        assertEquals(1, count);
        String actualDescription = jdbcTemplate.queryForObject("SELECT description FROM cars WHERE brand = ? AND model = ?",
                String.class, brand.name(), model);
        assertEquals(description, actualDescription);

        carTimesRepository.upsert(id);

        count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cars_update_times", Integer.class); 
        assertEquals(1, count);
        LocalDateTime actualTime = jdbcTemplate.queryForObject("SELECT updated_at FROM cars_update_times WHERE car_id = ?",
                LocalDateTime.class, id);
        assertNotEquals(time, actualTime);
    }
}
