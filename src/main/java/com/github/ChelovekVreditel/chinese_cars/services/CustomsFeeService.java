package com.github.ChelovekVreditel.chinese_cars.services;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.github.ChelovekVreditel.chinese_cars.exceptions.RateNotFoundException;
import com.github.ChelovekVreditel.chinese_cars.models.CustomsFeeRate;
import com.github.ChelovekVreditel.chinese_cars.repositories.CustomsFeeRateRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomsFeeService {

    private final CustomsFeeRateRepository customsFeeRateRepository;

    public BigDecimal calculate(BigDecimal customsValueRub) {
        CustomsFeeRate rate = customsFeeRateRepository.findRate(customsValueRub, LocalDate.now())
                .orElseThrow(() -> new RateNotFoundException("Ставка таможенного сбора не найдена"));

        return rate.feeRub();
    }
}
