package com.github.ChelovekVreditel.chinese_cars.repositories.CarConfigurations;

import java.util.List;
import java.util.Optional;

import com.github.ChelovekVreditel.chinese_cars.models.CarConfiguration;
import lombok.NonNull;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CarConfigurationRepository extends CrudRepository<@NonNull CarConfiguration, @NonNull Long>, 
                                                    CarConfigurationCustomRepository {

    @Query("SELECT id FROM cars_configurations WHERE car_id = :carId AND original_name = :name")
    public Optional<Long> findIdByCarIdAndOriginalName(@Param("carId") Long carId, 
                                               @Param("name") String originalName);

    public List<CarConfiguration> getCarConfigurationsByCarId(Long carId);
}
