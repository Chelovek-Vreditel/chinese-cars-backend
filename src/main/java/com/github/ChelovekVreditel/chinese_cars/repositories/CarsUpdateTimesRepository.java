package com.github.ChelovekVreditel.chinese_cars.repositories;

import com.github.ChelovekVreditel.chinese_cars.models.CarUpdateTime;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import lombok.NonNull;

public interface CarsUpdateTimesRepository extends CrudRepository<@NonNull CarUpdateTime, @NonNull Long>{

    @Modifying
    @Query("""
        INSERT INTO cars_update_times (car_id, updated_at)
        VALUES (:carId, NOW())
        ON CONFLICT (car_id) DO UPDATE SET 
           updated_at = EXCLUDED.updated_at; 
    """)
    public void upsert(@Param("carId") Long carId);
}
