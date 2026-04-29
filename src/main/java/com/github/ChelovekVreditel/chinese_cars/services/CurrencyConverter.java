package com.github.ChelovekVreditel.chinese_cars.services;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.github.ChelovekVreditel.chinese_cars.clients.cbr.ExchangeRateService;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CurrencyConverter {

    private final ExchangeRateService exchangeRateService;

    public BigDecimal convertCnyToRub(BigDecimal amountInCny) {
        if (amountInCny == null) {
            throw new IllegalArgumentException("Значение для перевода из CNY в RUB равно null.");
        }
        BigDecimal rate = exchangeRateService.getCnyToRubRate();
        return amountInCny
                .multiply(rate)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
