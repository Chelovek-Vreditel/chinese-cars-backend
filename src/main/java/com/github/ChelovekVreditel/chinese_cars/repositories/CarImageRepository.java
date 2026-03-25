package com.github.ChelovekVreditel.chinese_cars.repositories;

import com.github.ChelovekVreditel.chinese_cars.models.CarImage;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarImageRepository extends CrudRepository<@NonNull CarImage, @NonNull Long> {
}
