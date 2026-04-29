package com.github.ChelovekVreditel.chinese_cars.clients.cbr;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.io.StringReader;

import com.github.ChelovekVreditel.chinese_cars.clients.cbr.xml.ValCurs;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CbrXmlParser {

    private final WebClient webClient;

    public CbrXmlParser(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("www.cbr.ru")
                .build();
    }

    private static final String CBR_DAILY_PATH = "/scripts/XML_daily.asp";
    private static final String CNY_CHAR_CODE  = "CNY";
    private static final Charset WINDOWS_1251  = Charset.forName("windows-1251");

    private static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(ValCurs.class);
        } catch (JAXBException e) {
            throw new ExceptionInInitializerError("Ошибка инициализации JAXBContext: " + e.getMessage());
        }
    }

    public Mono<BigDecimal> fetchCnyRate() {
        return webClient.get()
                .uri(CBR_DAILY_PATH)
                .retrieve()
                .bodyToMono(byte[].class)
                .map(bytes -> new String(bytes, WINDOWS_1251))
                .map(this::parseXml)
                .map(this::extractCnyRate)
                .doOnSuccess(rate -> log.debug("Текущий курс CNY по данным ЦБР: {}", rate))
                .doOnError(e -> log.error("Получена ошибка при получении текущего курса CNY с API ЦБР: {}", e.getMessage()));
    }

    private ValCurs parseXml(String xml) {
        try {
            Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
            return (ValCurs) unmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            throw new RuntimeException("Ошибка парсинга XML ответа от ЦБР.", e);
        }
    }
    
    private BigDecimal extractCnyRate(ValCurs valCurs) {
        return valCurs.getValutes().stream()
                .filter(v -> CNY_CHAR_CODE.equals(v.getCharCode()))
                .findFirst()
                .map(v -> {
                    BigDecimal value   = new BigDecimal(v.getValue().replace(",", "."));
                    BigDecimal nominal = BigDecimal.valueOf(v.getNominal());
                    return value.divide(nominal, 6, RoundingMode.HALF_UP);
                })
                .orElseThrow(() -> new RuntimeException("Данные о курсе CNY не найдены в ответе от ЦБР."));
    }
}
