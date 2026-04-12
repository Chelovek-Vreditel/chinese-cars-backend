package com.github.ChelovekVreditel.chinese_cars.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.github.ChelovekVreditel.chinese_cars.configs.JdbcConfig;
import com.github.ChelovekVreditel.chinese_cars.enums.CarBrand;
import com.github.ChelovekVreditel.chinese_cars.models.Car;
import com.github.ChelovekVreditel.chinese_cars.models.CarConfiguration;
import com.github.ChelovekVreditel.chinese_cars.repositories.CarConfigurations.CarConfigurationRepository;
import com.github.ChelovekVreditel.chinese_cars.repositories.CarsConfigurationsUpdateTimes.CarsConfigurationsUpdateTimesRepository;

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
class CarConfigurationRepositoriesTest {

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
    private CarConfigurationRepository carConfigurationRepository;
    @Autowired
    private CarsConfigurationsUpdateTimesRepository carConfigurationTimesRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldInsertNewCarsConfigurationsAndTimes() {
        Car car = Car.builder()
            .id((long) 1)
            .brand(CarBrand.Audi)
            .originalModel("全新奥迪 A6L")
            .model("Comfort A")
            .basePriceCny(new BigDecimal("1200.00"))
            .build();
        jdbcTemplate.update("""
            INSERT INTO cars (id, brand, series, original_model, model, base_price_cny, description, source_url) VALUES (?,?,?,?,?,?,?,?)
            """,
            car.getId(), car.getBrand().name(), car.getSeries(), car.getOriginalModel(), car.getModel(),
            car.getBasePriceCny(), car.getDescription(), car.getSourceUrl()
        );

        CarConfiguration conf1 = CarConfiguration.builder()
            .carId((long) 1)
            .originalName("A3 Sportback 35TFSI 飞驰悦享型")
            .name("EXTREME")
            .basePriceCny(new BigDecimal("1200.00"))
            .build();

        CarConfiguration conf2 = CarConfiguration.builder()
            .carId((long) 1)
            .originalName("A3 Sportback 35TFSI 飞驰尊享型")
            .name("LOW PRICE")
            .basePriceCny(new BigDecimal("900.99"))
            .build();

        List<CarConfiguration> confs = List.of(conf1, conf2);
        carConfigurationRepository.batchUpsert(confs);

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cars_configurations", Integer.class);
        assertEquals(confs.size(), count);
        Long id1 = carConfigurationRepository.findIdByCarIdAndOriginalName(conf1.getCarId(), conf1.getOriginalName()).get();
        Long id2 = carConfigurationRepository.findIdByCarIdAndOriginalName(conf2.getCarId(), conf2.getOriginalName()).get();
        BigDecimal savedPrice = jdbcTemplate.queryForObject("SELECT base_price_cny FROM cars_configurations WHERE id = ?", 
            BigDecimal.class, id2);
        assertEquals(conf2.getBasePriceCny(), savedPrice);

        List<Long> ids = List.of(id1, id2);
        carConfigurationTimesRepository.batchUpsert(ids);
        count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cars_configurations_update_times", Integer.class);
        assertEquals(ids.size(), count);
    }

    @Test
    void shouldUpdateExistingRecords() {
        Car car = Car.builder()
            .id((long) 1)
            .brand(CarBrand.Audi)
            .originalModel("全新奥迪 A6L")
            .model("Comfort A")
            .basePriceCny(new BigDecimal("1200.00"))
            .build();
        jdbcTemplate.update("""
            INSERT INTO cars (id, brand, series, original_model, model, base_price_cny, description, source_url) VALUES (?,?,?,?,?,?,?,?)
            """,
            car.getId(), car.getBrand().name(), car.getSeries(), car.getOriginalModel(), car.getModel(),
            car.getBasePriceCny(), car.getDescription(), car.getSourceUrl()
        );

        Long id = (long) 1;
        CarConfiguration conf = CarConfiguration.builder()
            .id(id)
            .carId((long) 1)
            .originalName("A3 Sportback 35TFSI 飞驰悦享型")
            .name("EXTREME")
            .basePriceCny(new BigDecimal("1200.00"))
            .build();

        jdbcTemplate.update("INSERT INTO cars_configurations (id, car_id, original_name, name, base_price_cny) VALUES (?,?,?,?,?)",
                conf.getId(), conf.getCarId(), conf.getOriginalName(), conf.getName(), conf.getBasePriceCny());

        LocalDateTime time = LocalDateTime.of(2026, 2, 10, 10, 10, 0);

        jdbcTemplate.update("INSERT INTO cars_configurations_update_times (car_configuration_id, updated_at) VALUES (?,?)",
                conf.getId(), time);

        conf.setBasePriceCny(new BigDecimal("1400.20"));
        conf.setId(null);
        carConfigurationRepository.batchUpsert(List.of(conf));

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cars_configurations", Integer.class); 
        assertEquals(1, count);
        BigDecimal actualPrice = jdbcTemplate.queryForObject("""
                SELECT base_price_cny FROM cars_configurations WHERE 
                car_id = ? AND original_name = ?
                """,
                BigDecimal.class, conf.getCarId(), conf.getOriginalName());
        assertEquals(conf.getBasePriceCny(), actualPrice);

        carConfigurationTimesRepository.batchUpsert(List.of(id));

        count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cars_configurations_update_times", Integer.class); 
        assertEquals(1, count);
        LocalDateTime actualTime = jdbcTemplate.queryForObject("""
                SELECT updated_at FROM cars_configurations_update_times WHERE car_configuration_id = ?
                """,
                LocalDateTime.class, id);
        assertNotEquals(time, actualTime);
    }
}
