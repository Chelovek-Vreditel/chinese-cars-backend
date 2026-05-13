package com.github.ChelovekVreditel.chinese_cars.services;

import java.math.BigDecimal;

import com.github.ChelovekVreditel.chinese_cars.exceptions.RateNotFoundException;
import com.github.ChelovekVreditel.chinese_cars.repositories.CustomsFeeRateRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomsFeeService {

    private final CustomsFeeRateRepository customsFeeRateRepository;

    public BigDecimal calculate(BigDecimal costRub) {
        return customsFeeRateRepository.findRate(costRub)
            .orElseThrow(() -> new RateNotFoundException("Таможенная пошлина не найдена.")).feeRub();                
    }
}
