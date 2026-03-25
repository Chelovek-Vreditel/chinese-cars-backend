package com.github.ChelovekVreditel.chinese_cars.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("car_images")
public class CarImage {

    @Id
    private Long id;

    private Long carId;
    private String url;

    public CarImage(Long carId, String url) {
        this.carId = carId;
        this.url = url;
    }

    public CarImage() {}
}
