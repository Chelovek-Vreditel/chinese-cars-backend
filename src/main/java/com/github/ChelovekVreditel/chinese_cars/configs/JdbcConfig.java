package com.github.ChelovekVreditel.chinese_cars.configs;

import java.util.List;

import com.github.ChelovekVreditel.chinese_cars.enums.CarBrand;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;

@Configuration
public class JdbcConfig extends AbstractJdbcConfiguration {

    @Bean
    public NamingStrategy namingStrategy() {
        return new NamingStrategy() {
            @Override
            public String getColumnName(RelationalPersistentProperty property) {
                return camelToSnake(property.getName());
            }
        };
    }

    private String camelToSnake(String str) {
        return str.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    @Bean
    @Override
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(List.of(
            new CarBrandWritingConverter(),
            new CarBrandReadingConverter()
        ));
    }

    @WritingConverter
    static class CarBrandWritingConverter implements Converter<CarBrand, String> {
        @Override
        public String convert(CarBrand source) {
            return source.name();
        }
    }

    @ReadingConverter
    static class CarBrandReadingConverter implements Converter<String, CarBrand> {
        @Override
        public CarBrand convert(String source) {
            return CarBrand.valueOf(source);
        }
    }
}
