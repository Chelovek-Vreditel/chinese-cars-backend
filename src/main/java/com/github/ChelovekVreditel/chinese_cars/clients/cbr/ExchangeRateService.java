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

    @PostConstruct
    public void init() {
        log.info("Получение курса CNY/RUB при запуске...");
        try {
            BigDecimal rate = cbrXmlParser.fetchCnyRate().block(Duration.ofSeconds(15));
            if (rate != null) {
                cnyToRubRate.set(rate);
                log.info("Начальное соотношение CNY/RUB: 1 CNY = {} RUB", rate);
            }
        } catch (Exception e) {
            log.error("Ошибка при получении курса CNY/RUB при запуске: {}", e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 12 * * *")
    public void scheduledRateUpdate() {
        log.info("Запущено обновление курса CNY/RUB.");
        cbrXmlParser.fetchCnyRate()
                .subscribe(
                        rate -> {
                            cnyToRubRate.set(rate);
                            log.info("Обновлённый курс CNY/RUB: 1 CNY = {} RUB", rate);
                        },
                        error -> log.error("Ошибка при получении текущего курса CNY/RUB: {}", error.getMessage())
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
}
