package com.github.ChelovekVreditel.chinese_cars.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import com.github.ChelovekVreditel.chinese_cars.exceptions.RateNotFoundException;
import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationTechnicalSpecs;
import com.github.ChelovekVreditel.chinese_cars.models.ExciseRate;
import com.github.ChelovekVreditel.chinese_cars.repositories.ExciseRateRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExciseService {

    private final ExciseRateRepository exciseRateRepository;

    // Вызывается только для электромобилей
    public BigDecimal calculate(ConfigurationTechnicalSpecs specs) {
        ExciseRate rate = exciseRateRepository.findRate(specs.getEnginePowerHp(), LocalDate.now())
                .orElseThrow(() -> new RateNotFoundException("Ставка акциза не найдена"));

        return rate.rateRubPerHp()
                .multiply(BigDecimal.valueOf(specs.getEnginePowerHp()))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
