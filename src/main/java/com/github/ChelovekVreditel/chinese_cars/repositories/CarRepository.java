package com.github.ChelovekVreditel.chinese_cars.repositories;

import com.github.ChelovekVreditel.chinese_cars.models.Car;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends CrudRepository<@NonNull Car, @NonNull Long> {
}