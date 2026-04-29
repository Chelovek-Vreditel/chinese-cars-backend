package com.github.ChelovekVreditel.chinese_cars.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import com.github.ChelovekVreditel.chinese_cars.clients.cbr.ExchangeRateService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyConverterTest {

    @Test
    void convertCnyToRub_printsResult() {
        ExchangeRateService rateService = mock(ExchangeRateService.class);

        BigDecimal rate = new BigDecimal("1.30625");
        when(rateService.getCnyToRubRate()).thenReturn(rate);

        CurrencyConverter converter = new CurrencyConverter(rateService);

        BigDecimal amountCny = new BigDecimal("100");
        BigDecimal resultRub = converter.convertCnyToRub(amountCny);

        System.out.println("Конвертация " + amountCny + " CNY -> " + resultRub + " RUB (курс=" + rate + ")");

        assertNotNull(resultRub);
        assertEquals(new BigDecimal("130.63"), resultRub);
        assertEquals(2, resultRub.scale());

        verify(rateService, times(1)).getCnyToRubRate();
        verifyNoMoreInteractions(rateService);
    }
}
