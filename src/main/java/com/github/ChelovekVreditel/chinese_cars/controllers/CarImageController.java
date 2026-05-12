package com.github.ChelovekVreditel.chinese_cars.controllers;

import com.github.ChelovekVreditel.chinese_cars.dtos.FetchedImage;
import com.github.ChelovekVreditel.chinese_cars.services.CarImageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/images")
public class CarImageController {

    @Autowired
    private CarImageService carImageService;

    @GetMapping("/{carId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long carId) {
       FetchedImage fetchedImage = carImageService.getCarImageInBytes(carId);
       return ResponseEntity.ok()
           .contentType(MediaType.parseMediaType(fetchedImage.contentType()))
           .body(fetchedImage.bytes());
    }
}
