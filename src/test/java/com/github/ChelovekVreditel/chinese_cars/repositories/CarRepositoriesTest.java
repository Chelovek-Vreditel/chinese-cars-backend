package com.github.ChelovekVreditel.chinese_cars.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;

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
            .originalModel("全新奥迪 A6L")
            .model("super A")
            .basePriceCny(new BigDecimal("910.24"))
            .description("Хорошая машина")
            .sourceUrl("http://some.fake.url")
            .build();

        Car car2 = Car.builder()
            .brand(CarBrand.BMW)
            .originalModel("A8L Horch 创始人版")
            .model("KBM")
            .basePriceCny(new BigDecimal("1400.32"))
            .build();

        carRepository.upsert(car1.getBrand(), car1.getSeries(), car1.getOriginalModel(), car1.getModel(),
                car1.getBasePriceCny(), car1.getDescription(), car1.getSourceUrl());
        carRepository.upsert(car2.getBrand(), car2.getSeries(), car2.getOriginalModel(), car2.getModel(),
                car2.getBasePriceCny(), car2.getDescription(), car2.getSourceUrl());

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cars", Integer.class);
        assertEquals(2, count);
        Long id = carRepository.findIdByBrandAndOriginalModel(car1.getBrand(), car1.getOriginalModel()).get();
        String savedSeries = jdbcTemplate.queryForObject("SELECT series FROM cars WHERE id = ?", String.class, id);
        assertEquals(car1.getSeries(), savedSeries);

        carTimesRepository.upsert(id);
        count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cars_update_times", Integer.class);
        assertEquals(1, count);
    }

    @Test
    void shouldUpdateExistingRecords() {
        Long id = (long) 1;
        CarBrand brand = CarBrand.Audi;
        String series = "A";
        String originalModel = "全新奥迪 A6L";
        String model = "super";
        BigDecimal basePriceCny = new BigDecimal("1200.20");
        String description = "Изумительна";
        String sourceUrl = "http://some.fake.url";

        jdbcTemplate.update(
            "INSERT INTO cars (id, brand, series, original_model, model, base_price_cny, description, source_url) VALUES (?,?,?,?,?,?,?,?)",
            id, brand.name(), series, originalModel, model, basePriceCny, null, sourceUrl 
        );

        LocalDateTime time = LocalDateTime.of(2026, 1, 1, 12, 30, 0);

        jdbcTemplate.update(
            "INSERT INTO cars_update_times (car_id, updated_at) VALUES (?,?)",
            id, time
        );

        Car updatedCar = Car.builder()
            .brand(brand)
            .series(series)
            .originalModel(originalModel)
            .model(model)
            .basePriceCny(basePriceCny)
            .description(description)
            .sourceUrl(null)
            .build();

        carRepository.upsert(updatedCar.getBrand(), updatedCar.getSeries(), updatedCar.getOriginalModel(), updatedCar.getModel(),
                updatedCar.getBasePriceCny(), updatedCar.getDescription(), updatedCar.getSourceUrl());

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cars", Integer.class); 
        assertEquals(1, count);
        String actualDescription = jdbcTemplate.queryForObject("SELECT description FROM cars WHERE brand = ? AND original_model = ?",
                String.class, brand.name(), originalModel);
        assertEquals(description, actualDescription);

        carTimesRepository.upsert(id);

        count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cars_update_times", Integer.class); 
        assertEquals(1, count);
        LocalDateTime actualTime = jdbcTemplate.queryForObject("SELECT updated_at FROM cars_update_times WHERE car_id = ?",
                LocalDateTime.class, id);
        assertNotEquals(time, actualTime);
    }

    @Test
    void shouldReturnModelsByBrand() {
        Car car1 = Car.builder()
            .id((long) 1)
            .brand(CarBrand.Audi)
            .series("A")
            .originalModel("全新奥迪 A6L")
            .model("SUPER")
            .basePriceCny(new BigDecimal("1200.00"))
            .build();
        Car car2 = Car.builder()
            .id((long) 2)
            .brand(CarBrand.Mercedes)
            .series("M")
            .originalModel("A8L Horch 创始人版")
            .model("EXTREME")
            .basePriceCny(new BigDecimal("1890.10"))
            .build();
        List<Car> carsToSave = List.of(car1, car2);

        String sql = """
            INSERT INTO cars (id, brand, series, original_model, model, base_price_cny, description, source_url)
            VALUES (?,?,?,?,?,?,?,?)
        """;
        jdbcTemplate.batchUpdate(sql, carsToSave, 2, 
            (PreparedStatement ps, Car car) -> {
                ps.setLong(1, car.getId());
                ps.setString(2, car.getBrand().name());
                ps.setString(3, car.getSeries());
                ps.setString(4, car.getOriginalModel());
                ps.setString(5, car.getModel());
                ps.setBigDecimal(6, car.getBasePriceCny());
                ps.setNull(7, java.sql.Types.LONGVARCHAR);
                ps.setNull(8, java.sql.Types.LONGVARCHAR);
        });

        List<Car> response = carRepository.getCarsByBrand(CarBrand.Audi);
        assertEquals(1, response.size());
        assertEquals(car1.getModel(), response.get(0).getModel());
    }
}
