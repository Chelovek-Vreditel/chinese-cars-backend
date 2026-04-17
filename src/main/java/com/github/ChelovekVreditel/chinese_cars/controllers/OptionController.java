package com.github.ChelovekVreditel.chinese_cars.controllers;


import java.util.List;

import com.github.ChelovekVreditel.chinese_cars.dtos.ConfigurationOptionDto;
import com.github.ChelovekVreditel.chinese_cars.services.ConfigurationOptionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/options")
public class OptionController {

    @Autowired
    private ConfigurationOptionService optionService;

    @GetMapping("/{confId}")
    public ResponseEntity<List<ConfigurationOptionDto>> getOptionsByConfigurationId(@PathVariable Long confId) {
        try {
            List<ConfigurationOptionDto> options = optionService.getOptionsByConfigurationId(confId);
            return ResponseEntity.ok(options);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
