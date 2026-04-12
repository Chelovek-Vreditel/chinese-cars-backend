package com.github.ChelovekVreditel.chinese_cars.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import com.github.ChelovekVreditel.chinese_cars.configs.JdbcConfig;
import com.github.ChelovekVreditel.chinese_cars.enums.CarBrand;
import com.github.ChelovekVreditel.chinese_cars.models.Car;
import com.github.ChelovekVreditel.chinese_cars.models.CarConfiguration;
import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationOption;
import com.github.ChelovekVreditel.chinese_cars.repositories.ConfigurationOptions.ConfigurationOptionRepository;
import com.github.ChelovekVreditel.chinese_cars.repositories.ConfigurationOptionsUpdateTimes.ConfigurationOptionsUpdateTimesRepository;

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
class ConfigurationOptionRepositoriesTest {

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
    private ConfigurationOptionRepository configurationOptionRepository;
    @Autowired
    private ConfigurationOptionsUpdateTimesRepository configurationOptionTimesRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldInsertNewConfigurationOptionsAndTimes() {
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
        CarConfiguration conf = CarConfiguration.builder()
            .id((long) 1)
            .carId(car.getId())
            .originalName("A3 Sportback 35TFSI 飞驰悦享型")
            .name("EXTREME")
            .basePriceCny(new BigDecimal("1200.00"))
            .build();
        jdbcTemplate.update("""
            INSERT INTO cars_configurations (id, car_id, original_name, name, base_price_cny) VALUES (?,?,?,?,?)
            """,
            conf.getId(), conf.getCarId(), conf.getOriginalName(), conf.getName(), conf.getBasePriceCny()
        );

        ConfigurationOption option1 = ConfigurationOption.builder()
            .configurationId((long) 1)
            .category("Внутренности")
            .originalName("倒车影像系统")
            .name("Двигатель V8")
            .value("included")
            .build();
        ConfigurationOption option2 = ConfigurationOption.builder()
            .configurationId((long) 1)
            .originalName("外后视镜电动调整、加热、电动折叠、自动防眩目、带记忆功能")
            .name("Сидения с подогревом")
            .value("is_optional")
            .priceCny(new BigDecimal("100.80"))
            .build();
        List<ConfigurationOption> options = List.of(option1, option2);

        configurationOptionRepository.batchUpsert(options);

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM configuration_options", Integer.class);
        assertEquals(options.size(), count);

        List<SimpleEntry<Long, String>> searchingProperties = options.stream()
            .map(o -> new AbstractMap.SimpleEntry<>(o.getConfigurationId(), o.getOriginalName()))
            .toList();
        List<Long> ids = configurationOptionRepository.findIdsByConfigurationIdAndOriginalName(searchingProperties);


        String savedValue = jdbcTemplate.queryForObject("SELECT value FROM configuration_options WHERE id = ?", 
            String.class, ids.get(0));
        assertEquals(option1.getValue(), savedValue);

        configurationOptionTimesRepository.batchUpsert(ids);
        count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM configuration_options_update_times", Integer.class);
        assertEquals(ids.size(), count);
    }

    @Test
    void shouldUpdateExistingRecord() {
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
        CarConfiguration conf = CarConfiguration.builder()
            .id((long) 1)
            .carId(car.getId())
            .originalName("A3 Sportback 35TFSI 飞驰悦享型")
            .name("EXTREME")
            .basePriceCny(new BigDecimal("1200.00"))
            .build();
        jdbcTemplate.update("""
            INSERT INTO cars_configurations (id, car_id, original_name, name, base_price_cny) VALUES (?,?,?,?,?)
            """,
            conf.getId(), conf.getCarId(), conf.getOriginalName(), conf.getName(), conf.getBasePriceCny()
        );

        Long id = (long) 1;
        ConfigurationOption option = ConfigurationOption.builder()
            .id(id)
            .configurationId((long) 1)
            .originalName("倒车影像系统")
            .name("Двигатель V8")
            .value("included")
            .build();

        jdbcTemplate.update("""
            INSERT INTO configuration_options (id, configuration_id, category, original_name, name, value, price_cny) VALUES (?,?,?,?,?,?,?)
            """,
            option.getId(), option.getConfigurationId(), option.getCategory(), option.getOriginalName(),
            option.getName(), option.getValue(), option.getPriceCny()
        );

        LocalDateTime time = LocalDateTime.of(2026, 2, 10, 10, 10, 0);

        jdbcTemplate.update("INSERT INTO configuration_options_update_times (configuration_option_id, updated_at) VALUES (?,?)",
                option.getId(), time);

        option.setValue("none");
        option.setId(null);
        configurationOptionRepository.batchUpsert(List.of(option));

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM configuration_options", Integer.class); 
        assertEquals(1, count);
        String actualValue = jdbcTemplate.queryForObject("""
            SELECT value FROM configuration_options WHERE id = ?
            """,
            String.class, id
        );
        assertEquals(option.getValue(), actualValue);

        configurationOptionTimesRepository.batchUpsert(List.of(id));

        count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM configuration_options_update_times", Integer.class); 
        assertEquals(1, count);
        LocalDateTime actualTime = jdbcTemplate.queryForObject("""
                SELECT updated_at FROM configuration_options_update_times WHERE configuration_option_id = ?
                """,
                LocalDateTime.class, id);
        assertNotEquals(time, actualTime);
    }
}
