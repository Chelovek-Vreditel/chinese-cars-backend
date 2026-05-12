package com.github.ChelovekVreditel.chinese_cars.services;

import java.time.Duration;
import java.util.Optional;

import javax.net.ssl.SSLException;

import com.github.ChelovekVreditel.chinese_cars.dtos.FetchedImage;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageFetchService {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration READ_TIMEOUT    = Duration.ofSeconds(15);
    private static final Duration SSL_TIMEOUT = Duration.ofSeconds(15);
    private static final Duration BLOCK_TIMEOUT = Duration.ofSeconds(40);

    private final WebClient webClient = createWebClient();

    private static WebClient createWebClient() {
        SslContext sslContext;
        try {
            sslContext = SslContextBuilder.forClient().build();
        } catch (SSLException e) {
            throw new IllegalStateException("Не удалось создать SSL-контекст", e);
        }

        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) CONNECT_TIMEOUT.toMillis())
            .secure(spec -> spec
                .sslContext(sslContext)
                .handshakeTimeout(SSL_TIMEOUT))
            .responseTimeout(READ_TIMEOUT)
            .followRedirect(true);

        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }

    public Optional<FetchedImage> fetch(String imageUrl) {
        try {
            ResponseEntity<byte[]> response = webClient.get()
                .uri(imageUrl)
                .retrieve()
                .toEntity(byte[].class)
                .block(BLOCK_TIMEOUT);

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
