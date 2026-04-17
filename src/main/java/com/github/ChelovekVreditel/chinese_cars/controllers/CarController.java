package com.github.ChelovekVreditel.chinese_cars.controllers;

import java.util.List;

import com.github.ChelovekVreditel.chinese_cars.dtos.CarDto;
import com.github.ChelovekVreditel.chinese_cars.services.CarService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/models")
public class CarController {

    @Autowired
    private CarService carService;

    @GetMapping("/{brand}")
    public ResponseEntity<List<CarDto>> getCarsByBrand(@PathVariable String brand) {
        try {
            List<CarDto> cars = carService.getCarsByBrand(brand);
            return ResponseEntity.ok(cars);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
