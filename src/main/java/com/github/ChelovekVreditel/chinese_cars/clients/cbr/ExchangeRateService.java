package com.github.ChelovekVreditel.chinese_cars.clients.cbr;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class ExchangeRateService {

    private final CbrXmlParser cbrXmlParser;

    private final AtomicReference<BigDecimal> cnyToRubRate = new AtomicReference<>();
    private final AtomicReference<BigDecimal> eurToRubRate = new AtomicReference<>();

    @PostConstruct
    public void init() {
        log.info("Получение курсов CNY/RUB и EUR/RUB при запуске...");
        try {
            BigDecimal rate = cbrXmlParser.fetchRate("CNY").block(Duration.ofSeconds(15));
            if (rate != null) {
                cnyToRubRate.set(rate);
                log.info("Начальное соотношение CNY/RUB: 1 CNY = {} RUB", rate);
            }
            rate = cbrXmlParser.fetchRate("EUR").block(Duration.ofSeconds(15));
            if (rate != null) {
                eurToRubRate.set(rate);
                log.info("Начальное соотношение EUR/RUB: 1 EUR = {} RUB", rate);
            }
        } catch (Exception e) {
            log.error("Ошибка при получении курсов при запуске: {}", e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 12 * * *")
    public void scheduledRateUpdate() {
        log.info("Запущено обновление курса CNY/RUB.");
        cbrXmlParser.fetchRate("CNY")
                .subscribe(
                        rate -> {
                            cnyToRubRate.set(rate);
                            log.info("Обновлённый курс CNY/RUB: 1 CNY = {} RUB", rate);
                        },
                        error -> log.error("Ошибка при получении текущего курса CNY/RUB: {}", error.getMessage())
                );
        log.info("Запущено обновление курса EUR/RUB.");
        cbrXmlParser.fetchRate("EUR")
                .subscribe(
                        rate -> {
                            cnyToRubRate.set(rate);
                            log.info("Обновлённый курс EUR/RUB: 1 CNY = {} RUB", rate);
                        },
                        error -> log.error("Ошибка при получении текущего курса EUR/RUB: {}", error.getMessage())
                );

    }

    public BigDecimal getCnyToRubRate() {
        BigDecimal rate = cnyToRubRate.get();
        if (rate == null) {
            throw new IllegalStateException(
                "Курс CNY/RUB сейчас не доступен: значение не инициализировано."
            );
        }
        return rate;
    }

    public BigDecimal getEurToRubRate() {
        BigDecimal rate = eurToRubRate.get();
        if (rate == null) {
            throw new IllegalStateException(
                "Курс EUR/RUB сейчас не доступен: значение не инициализировано."
            );
        }
        return rate;
    }
}
