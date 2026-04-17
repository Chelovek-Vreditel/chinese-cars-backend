package com.github.ChelovekVreditel.chinese_cars.controllers;

import java.util.List;

import com.github.ChelovekVreditel.chinese_cars.dtos.CarConfigurationDto;
import com.github.ChelovekVreditel.chinese_cars.services.CarConfigurationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/configurations")
public class ConfigurationController {

    @Autowired
    private CarConfigurationService configurationService;

    @GetMapping("/{carId}")
    public ResponseEntity<List<CarConfigurationDto>> getConfigurationsByCarId(@PathVariable Long carId) {
        try {
            List<CarConfigurationDto> confs = configurationService.getConfiguartionsByCarId(carId);
            return ResponseEntity.ok(confs);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
