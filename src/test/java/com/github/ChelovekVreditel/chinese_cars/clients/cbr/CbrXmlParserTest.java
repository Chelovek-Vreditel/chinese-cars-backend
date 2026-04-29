package com.github.ChelovekVreditel.chinese_cars.clients.cbr;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class CbrXmlParserTest {

    @Test
    void fetchCnyRate_liveFromCbr_printsParsedRate() {
        CbrXmlParser parser = new CbrXmlParser(WebClient.builder());

        BigDecimal rate = parser.fetchCnyRate().block(Duration.ofSeconds(15));
        System.out.println("Курс CNY/RUB = " + rate);

        assertNotNull(rate);
        assertTrue(rate.compareTo(BigDecimal.ZERO) > 0);
    }
}
