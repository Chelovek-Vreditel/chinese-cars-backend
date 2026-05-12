package com.github.ChelovekVreditel.chinese_cars.services;

import java.time.Duration;
import java.util.Optional;

import com.github.ChelovekVreditel.chinese_cars.dtos.FetchedImage;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageFetchService {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration READ_TIMEOUT    = Duration.ofSeconds(10);

    private final WebClient webClient = WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(
            HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) CONNECT_TIMEOUT.toMillis())
                .responseTimeout(READ_TIMEOUT)
                .followRedirect(true)
        ))
        .build();

    public Optional<FetchedImage> fetch(String imageUrl) {
        try {
            ResponseEntity<byte[]> response = webClient.get()
                .uri(imageUrl)
                .retrieve()
                .toEntity(byte[].class)
                .block(READ_TIMEOUT);

            if (response == null || response.getBody() == null) {
                return Optional.empty();
            }

            String contentType = Optional
                .ofNullable(response.getHeaders().getContentType())
                .map(MediaType::toString)
                .orElse("image/jpeg");

            if (!contentType.startsWith("image/")) {
                log.warn("Ожидалось изображение, получен {}: {}", contentType, imageUrl);
                return Optional.empty();
            }

            return Optional.of(new FetchedImage(response.getBody(), contentType));

        } catch (Exception e) {
            log.warn("Не удалось скачать изображение: {} — {}", imageUrl, e.getMessage());
            return Optional.empty();
        }
    }
}
