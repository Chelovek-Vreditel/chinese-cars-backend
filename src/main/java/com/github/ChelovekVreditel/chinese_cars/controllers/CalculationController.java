package com.github.ChelovekVreditel.chinese_cars.controllers;

import com.github.ChelovekVreditel.chinese_cars.dtos.CalculationRequest;
import com.github.ChelovekVreditel.chinese_cars.dtos.CalculationResponse;
import com.github.ChelovekVreditel.chinese_cars.services.CalculationFacade;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/calculations")
@RequiredArgsConstructor
public class CalculationController {

    private final CalculationFacade calculationFacade;

    @PostMapping
    public ResponseEntity<CalculationResponse> calculate(@RequestBody @Valid CalculationRequest request) {
        return ResponseEntity.ok(calculationFacade.calculate(request));
    }
}
