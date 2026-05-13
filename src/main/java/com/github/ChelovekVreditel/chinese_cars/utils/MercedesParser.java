package com.github.ChelovekVreditel.chinese_cars.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.github.ChelovekVreditel.chinese_cars.dtos.CarWithImageSource;
import com.github.ChelovekVreditel.chinese_cars.dtos.ConfigurationDetails;
import com.github.ChelovekVreditel.chinese_cars.enums.CarBrand;
import com.github.ChelovekVreditel.chinese_cars.exceptions.ParseException;
import com.github.ChelovekVreditel.chinese_cars.models.Car;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

@Component
public class MercedesParser {

    private final WebClient webClient;
    private static final BigDecimal multiplier = new BigDecimal("10000");

    public MercedesParser() {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(configurer -> configurer.defaultCodecs()
                .maxInMemorySize(2 * 1024 * 1024))
            .build();

        this.webClient = WebClient.builder()
            .exchangeStrategies(strategies)
            .build();
    }

    private String fetchJson(String url) {
        return webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }

    public List<CarWithImageSource> extractCarsModels(String globalDataUrl) {
        String json = fetchJson(globalDataUrl);
        
        JsonMapper mapper = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();
        
        JsonNode root = mapper.readTree(json);

        List<CarWithImageSource> result = new ArrayList<>();

        CarBrand carBrand = CarBrand.Mercedes;
        JsonNode seriesList = root.path("data").path("vehicleList").path("vehicleList").path("data").path("attributes")
            .path("OWVehicleBody");
        if (!seriesList.isArray()) throw new ParseException("Не найден массив с сериями моделей Mercedes.");
        for (JsonNode seriesNode : seriesList) {
            String seriesTitle = seriesNode.path("title").asString();
            JsonNode modelList = seriesNode.path("OWVehicleClass");
            for (JsonNode modelNode : modelList) {
                String originalModel = modelNode.path("title").asString();
                JsonNode attributesNode = modelNode.path("OWVehicleModel").get(0).path("vehicleDetail").path("data")
                    .path("attributes");
                String description = attributesNode.path("name").asString();
                String sourceUrl = attributesNode.path("detailLink").path("data").path("attributes").path("link")
                    .path("link").asString();
                String rawPrice;
                try {
                    rawPrice = attributesNode.path("vehicleData").path("model").path("data").path("attributes")
                            .path("publishedMsrp").asString();
                } catch (Exception e) {
                    rawPrice = null;
                }
                String imgUrl = attributesNode.path("vehicleData").path("vehicleImagePC").path("data")
                    .path("attributes").path("formats").path("thumbnail").path("url").asString();
                Car rawCar = Car.builder()
                    .brand(carBrand)
                    .series(seriesTitle)
                    .originalModel(originalModel)
                    .description(description)
                    .sourceUrl(sourceUrl)
                    .build();
                if (rawPrice != null && !rawPrice.trim().isEmpty()) {
                    rawCar.setBasePriceCny((new BigDecimal(rawPrice.replaceAll("[^0-9.-]", "")).multiply(multiplier)));
                }

                result.add(new CarWithImageSource(rawCar, imgUrl));
            }
        }

        return result;
    }

    // public List<ConfigurationDetails> extractConfigurationsDetails(String url) {
    //     String json = fetchJson(url);
    //
    //     JsonMapper mapper = JsonMapper.builder()
    //         .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    //         .enable(SerializationFeature.INDENT_OUTPUT)
    //         .build();
    //
    //     JsonNode root = mapper.readTree(json);
    //
    //     List<ConfigurationDetails> result = new ArrayList<>();
    //
    // }
}
