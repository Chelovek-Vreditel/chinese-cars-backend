package com.github.ChelovekVreditel.chinese_cars.utils.impl;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

import com.github.ChelovekVreditel.chinese_cars.dtos.ConfigurationDetails;
import com.github.ChelovekVreditel.chinese_cars.exceptions.TranslateException;
import com.github.ChelovekVreditel.chinese_cars.models.Car;
import com.github.ChelovekVreditel.chinese_cars.models.CarConfiguration;
import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationOption;
import com.github.ChelovekVreditel.chinese_cars.utils.Translator;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Profile("mvp")
@Component
@Slf4j
@RequiredArgsConstructor
public class MyMemoryTranslatorImpl implements Translator {

    private RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(10));
        return new RestTemplate(factory);
    }

    private final RestTemplate restTemplate = createRestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${mymemory.email.use}")
    private String useMymemoryEmail;
    @Value("${mymemory.email.name}")
    private String mymemoryEmail;

    // Параметры для метода translateWithRetry
    private static final int MAX_APPLYES = 3;
    private static final int DELAY_MS = 500;
    
    private final LoadingCache<String, String> cache = Caffeine.newBuilder()
        .maximumSize(10000)
        .build(this::translateWithRetry);

    private String translateWithExternalAPI(String originalStr) throws Exception {
        String url = String.format(
            "https://api.mymemory.translated.net/get?q=%s&langpair=%s",
            UriUtils.encode(originalStr, StandardCharsets.UTF_8),
            UriUtils.encode("zh-CN|ru", StandardCharsets.UTF_8)
        );
        if (useMymemoryEmail.equals("true") && mymemoryEmail != null && !mymemoryEmail.isBlank()) {
            url = url + "&de=" + mymemoryEmail;
        }

        URI uri = URI.create(url);
        String response = restTemplate.getForObject(uri, String.class);

        JsonNode root = mapper.readTree(response);

        if (root.has("responseData")) {
            JsonNode data = root.get("responseData");
            if (data.has("translatedText")) {
                return data.get("translatedText").asString();
            }
        }

        throw new TranslateException("Неожиданная структура ответа.");
    }
    
    private String translateWithRetry(String originalStr) {
        if (originalStr == null || originalStr.isBlank()) {
            return "";
        }

        for (int apply = 1; apply <= MAX_APPLYES; apply++) {
            try {
                return this.translateWithExternalAPI(originalStr);
            }
            catch (Exception e) {
                log.error("В ходе перевода получена ошибка: " + e.getMessage());
                if (apply < MAX_APPLYES) {
                    try {
                        Thread.sleep(DELAY_MS);
                    }
                    catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Перевод прерван.");
                        return originalStr;
                    }        
                } 
            }
        }

        log.error("Возникли непредвиденные проблемы при переводе.");
        return originalStr;
    }

    @Override
    public void translateCar(Car car) {
        Optional.ofNullable(car.getSeries())
            .map(String::strip)
            .filter(s -> !s.isEmpty())
            .ifPresent(series -> car.setSeries(cache.get(series)));
        Optional.ofNullable(car.getOriginalModel())
            .map(String::strip)
            .filter(s -> !s.isEmpty())
            .ifPresent(model -> car.setModel(cache.get(model)));
        Optional.ofNullable(car.getDescription())
            .map(String::strip)
            .filter(s -> !s.isEmpty())
            .ifPresent(description -> car.setDescription(cache.get(description)));
    }

    @Override
    public void translateCarConfiguration(CarConfiguration carConfiguration) {
        Optional.ofNullable(carConfiguration.getOriginalName())
            .map(String::strip)
            .filter(s -> !s.isEmpty())
            .ifPresent(name -> carConfiguration.setName(cache.get(name)));
    }

    @Override
    public void translateConfigurationOption(ConfigurationOption configurationOption) {
        Optional.ofNullable(configurationOption.getCategory())
            .map(String::strip)
            .filter(s -> !s.isEmpty())
            .ifPresent(category -> configurationOption.setCategory(cache.get(category)));
        Optional.ofNullable(configurationOption.getOriginalName())
            .map(String::strip)
            .filter(s -> !s.isEmpty())
            .ifPresent(name -> configurationOption.setName(cache.get(name)));
        Optional.ofNullable(configurationOption.getValue())
            .map(String::strip)
            .filter(s -> !s.isEmpty())
            .filter(s -> (!s.equals("included") && !s.equals("is_optional") && !s.equals("none")))
            .ifPresent(value -> configurationOption.setValue(cache.get(value)));
    }

    @Override
    public void translateConfigurationDetails(ConfigurationDetails configurationDetails) {
        this.translateCarConfiguration(configurationDetails.getCarConfiguration());
        for (ConfigurationOption option : configurationDetails.getConfigurationOptions()) {
            this.translateConfigurationOption(option);
        }
    }

}
